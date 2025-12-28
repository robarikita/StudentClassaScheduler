<%@ page import="java.util.List" %>
    <%@ page import="your.package.Course" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>Courses</title>
                <style>
                    table {
                        width: 80%;
                        border-collapse: collapse;
                        margin: 20px auto;
                    }

                    th,
                    td {
                        border: 1px solid #333;
                        padding: 8px;
                        text-align: center;
                    }

                    th {
                        background-color: #f2f2f2;
                    }

                    a,
                    input[type=submit] {
                        padding: 5px 10px;
                        text-decoration: none;
                        margin: 2px;
                    }
                </style>
            </head>

            <body>
                <h2 style="text-align:center;">All Courses</h2>
                <div style="text-align:center; margin-bottom:20px;">
                    <a href="add-course.html">Add New Course</a>
                </div>

                <% List<Course> courses = (List<Course>) request.getAttribute("courses");
                        if (courses != null && !courses.isEmpty()) {
                        %>
                        <table>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Credits</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                            <% for (Course course : courses) { %>
                                <tr>
                                    <td>
                                        <%= course.getId() %>
                                    </td>
                                    <td>
                                        <%= course.getName() %>
                                    </td>
                                    <td>
                                        <%= course.getDescription() %>
                                    </td>
                                    <td>
                                        <%= course.getCredits() %>
                                    </td>
                                    <td>
                                        <a href="edit-course.jsp?id=<%= course.getId() %>">Edit</a>
                                    </td>
                                    <td>
                                        <form action="delete-course" method="post"
                                            onsubmit="return confirm('Are you sure?');">
                                            <input type="hidden" name="id" value="<%= course.getId() %>">
                                            <input type="submit" value="Delete">
                                        </form>
                                    </td>
                                </tr>
                                <% } %>
                        </table>
                        <% } else { %>
                            <p style="text-align:center;">No courses available.</p>
                            <% } %>
            </body>

            </html>