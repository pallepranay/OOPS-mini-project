import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
abstract class car{
    String car_no;
    String model;
    int avalability;
    int charges;

    public car(String car_no, String model, int avalability, int charges) {
        this.car_no = car_no;
        this.model = model;
        this.avalability = avalability;
        this.charges = charges;
    }
    public abstract void discount();
}
class discountCar extends car{
    public discountCar(String car_no, String model, int avalability, int charges) {
        super(car_no, model, avalability, charges);
    }

    public void discount(){
        charges = charges/2;
    }
}

class customer{
    int cust_id;
    String name;
    String address;
    String phone_no;

    public customer(int cust_id, String name, String address, String phone_no) {
        this.cust_id = cust_id;
        this.name = name;
        this.address = address;
        this.phone_no = phone_no;
    }
}
class rental{
    int cust_id;
    String car_no;
    String start;
    String end;
    int total_fee;

    public rental(int cust_id, String car_no, String start, String end) {
        this.cust_id = cust_id;
        this.car_no = car_no;
        this.start = start;
        this.end = end;
    }
}
class ConnectionFactory {
    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        String url ="jdbc:mysql://localhost:3306/project";
        String uname = "root";
        String password ="MySQL&9146";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url,uname,password);
    }
}



public class Main {

    public static void initialize(String[] args) throws SQLException, ClassNotFoundException, IOException {
        try {
            Connection con = ConnectionFactory.createConnection();
            Statement st = con.createStatement();
            String query = "CREATE TABLE customer (cust_id int, name VARCHAR(20),address VARCHAR(100),phone_no varchar(10));";
            String query2 = "CREATE TABLE cars (car_no varchar(10), model VARCHAR(20),availability TINYINT(1),charges int);";
            String query3 = "CREATE TABLE rental (cust_id int,car_no varchar(10),start DATE,end DATE,total_fee int);";
            st.execute(query);
            st.execute(query2);
            st.execute(query3);
            String file = "src\\carData.csv";
            Scanner sin = new Scanner(new BufferedReader(new FileReader(file)));
            String str = "";
            while (sin.hasNext()) {
                str = sin.nextLine();
                String[] tokens = str.split(",");
                PreparedStatement ps = con.prepareStatement("insert into cars(car_no,model,availability,charges) values(?,?,?,?)");
                ps.setString(1, tokens[0]);
                ps.setString(2, tokens[1]);
                ps.setString(3, tokens[2]);
                ps.setString(4, tokens[3]);
                ps.executeUpdate();
                st.close();
            }
            System.out.println("Program intialised! tables have been made !");
        }
        catch (Exception e){
            System.out.println("Program have already been intialised!");
        }
        }
    public static void printHelp(){
        System.out.println("Help for commands:");
        System.out.println("-initialize for initializing the program");
        System.out.println("-b <recordType> <string> for operation on <recordType> and search <string>");
        System.out.println("-h (or any) for help menu");
    }

    public static void register(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Connection con = ConnectionFactory.createConnection();
        Statement st = con.createStatement();
        Statement st2 = con.createStatement();
        String query = "select max(cust_id) from customer;";
        String query2 = "select * from customer;";

        ResultSet rs = st.executeQuery(query);
        ResultSet rs2 = st2.executeQuery(query2);

        rs.next();
        int maxId = rs.getInt("max(cust_id)");
        int valid =0;
        while(rs2.next()){
            String name= rs2.getString("name");
            String phoneNo= rs2.getString("phone_no");
            if(Objects.equals(args[1], name) && Objects.equals(args[3], phoneNo)){
                System.out.println("User is already registered ! ");
                valid = 0;
                break;
            }
            else{
                valid=1;
            }
        }
        if(valid ==1){
            PreparedStatement ps = con.prepareStatement("insert into customer(cust_id, name,address,phone_no) values(?,?,?,?)");
            ps.setInt(1, maxId+1);
            ps.setString(2, args[1]);
            ps.setString(3, args[2]);
            ps.setString(4, args[3]);
            ps.executeUpdate();
            System.out.println("Registration done !");

            st.close();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Connection con = ConnectionFactory.createConnection();
            switch (args[0]) {
                case "-initialize":
                    initialize(args);
                    break;
                case "-register":
                    register(args);
                    break;
                default:
                    printHelp();
                    break;
            }
        con.close();
    }
}
