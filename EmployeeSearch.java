import java.sql.*;
import java.util.Scanner;

public class EmployeeSearch {

    static Connection con;

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/company";
        String user = "root";
        String password = "1234";
        Scanner sc = new Scanner(System.in);

        try {
            con = DriverManager.getConnection(url, user, password);

            int choice;
            do {
                System.out.println("\n--- Employee Search ---");
                System.out.println("1. Search by name");
                System.out.println("2. Filter by department");
                System.out.println("3. Salary range");
                System.out.println("4. Sort by salary");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Enter name: ");
                        String name = sc.nextLine();
                        run("SELECT * FROM employees WHERE emp_name LIKE ?",
                            "%" + name + "%");
                        break;
                    case 2:
                        System.out.print("Enter department: ");
                        String dept = sc.nextLine();
                        run("SELECT * FROM employees WHERE department = ?", dept);
                        break;
                    case 3:
                        System.out.print("Minimum salary: ");
                        double min = Double.parseDouble(sc.nextLine());
                        System.out.print("Maximum salary: ");
                        double max = Double.parseDouble(sc.nextLine());
                        PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM employees WHERE salary BETWEEN ? AND ?");
                        ps.setDouble(1, min);
                        ps.setDouble(2, max);
                        show(ps.executeQuery());
                        break;
                    case 4:
                        System.out.print("Order (ASC/DESC): ");
                        String order = sc.nextLine().equalsIgnoreCase("DESC") ? "DESC" : "ASC";
                        Statement st = con.createStatement();
                        show(st.executeQuery(
                            "SELECT * FROM employees ORDER BY salary " + order));
                        break;
                    case 5:
                        System.out.println("Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } while (choice != 5);

            con.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    static void run(String sql, String value) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, value);
        show(ps.executeQuery());
    }

    static void show(ResultSet rs) throws SQLException {
        System.out.printf("%-8s %-12s %-12s %-10s%n", "ID", "Name", "Department", "Salary");
        int count = 0;
        while (rs.next()) {
            System.out.printf("%-8d %-12s %-12s %-10.2f%n",
                rs.getInt("emp_id"),
                rs.getString("emp_name"),
                rs.getString("department"),
                rs.getDouble("salary"));
            count++;
        }
        if (count == 0) System.out.println("No employees found.");
    }
}