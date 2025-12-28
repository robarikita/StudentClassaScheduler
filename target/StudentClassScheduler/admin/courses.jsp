<%@ page import="java.util.List" %>
<%@ page import="com.roba.scheduler.model.Course" %>
<%@ page import="com.roba.scheduler.model.User" %>
<%
// Check if user is logged in as admin
if (session.getAttribute("user") == null) {
    response.sendRedirect("../../login.html");
    return;
}

User user = (User) session.getAttribute("user");
if (!"admin".equals(user.getRole())) {
    response.sendRedirect("../../login.html");
    return;
}

List<Course> courses = (List<Course>) request.getAttribute("courses");
if (courses == null) {
    response.sendRedirect("ListCoursesServlet");
    return;
}
%>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Courses</title>
    <style>
        body { font-family: Arial; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        .btn { padding: 5px 10px; text-decoration: none; border-radius: 3px; }
        .btn-add { background-color: #008CBA; color: white; padding: 10px 15px; }
        .btn-edit { background-color: #f0ad4e; color: white; }
        .btn-delete { background-color: #d9534f; color: white; }
        .error { color: red; background-color: #f8d7da; padding: 10px; margin: 10px 0; }
        .success { color: green; background-color: #d4edda; padding: 10px; margin: 10px 0; }
    </style>
    <script>
        function confirmDelete() {
            return confirm('Are you sure?');
        }
    </script>
</head>
<body>
    <h1>Manage Courses</h1>
    <a href="dashboard.jsp">Back to Dashboard</a>
    <br><br>
    <a href="add-course.html" class="btn btn-add">Add New Course</a>

    <h2>Course List</h2>

    <%
    String message = (String) session.getAttribute("message");
    if (message != null) {
    %>
        <div class="success"><%= message %></div>
    <%
        session.removeAttribute("message");
    }
    %>

    <% if (courses.isEmpty()) { %>
        <p>No courses found.</p>
    <% } else { %>
        <table>
            <tr>
                <th>ID</th>
                <th>Course Code</th>
                <th>Course Name</th>
                <th>Description</th>
                <th>Credits</th>
                <th>Actions</th>
            </tr>
            <% for (Course course : courses) { %>
            <tr>
                <td><%= course.getCourseId() %></td>
                <td><%= course.getCourseCode() %></td>
                <td><%= course.getCourseName() %></td>
                <td><%= course.getDescription() != null ? course.getDescription() : "" %></td>
                <td><%= course.getCredits() %></td>
                <td>
<a href="${pageContext.request.contextPath}/admin/EditCourseServlet?courseId=<%= course.getCourseId() %>"
   class="btn">Edit</a>
   <form action="DeleteCourseServlet" method="post" style="display: inline;">
                        <input type="hidden" name="courseId" value="<%= course.getCourseId() %>">
                        <button type="submit" class="btn btn-delete" onclick="return confirmDelete();">Delete</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </table>
    <% } %>
</body>
</html>