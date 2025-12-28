<%@ page import="java.sql.*" %>
    <!DOCTYPE html>
    <html>

    <head>
        <title>System Test</title>
    </head>

    <body>
        <h1>System Test Page</h1>
        <p>Current Time: <%= new java.util.Date() %>
        </p>
        <p>Context Path: <%= request.getContextPath() %>
        </p>

        <h2>Database Connection Test:</h2>
        <% try { Class.forName("com.mysql.cj.jdbc.Driver"); Connection
            conn=DriverManager.getConnection( "jdbc:mysql://localhost:3306/scheduler_db" , "root" , "" ); out.print("<p
            style='color:green'>✓ Database Connected Successfully</p>");

            // Test tables
            String[] tables = {"users", "students", "courses"};
            for(String table : tables) {
            try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
            rs.next();
            out.print("<p>✓ " + table + " table: " + rs.getInt(1) + " records</p>");
            } catch(Exception e) {
            out.print("<p style='color:red'>✗ " + table + " table error: " + e.getMessage() + "</p>");
            }
            }
            conn.close();
            } catch(Exception e) {
            out.print("<p style='color:red'>✗ Database Error: " + e.getMessage() + "</p>");
            }
            %>

            <h2>Test Links:</h2>
            <ul>
                <li><a href="login.html">Login Page</a></li>
                <li><a href="index.html">Home Page</a></li>
                <li><a href="admin/dashboard.html">Admin Dashboard</a> (login required)</li>
                <li><a href="admin/ListCoursesServlet">Course List Servlet</a> (login required)</li>
            </ul>
    </body>

    </html>