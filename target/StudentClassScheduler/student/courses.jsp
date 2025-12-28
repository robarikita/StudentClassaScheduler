<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.*, com.roba.scheduler.model.Course" %>
        <% List<Course> courses = (List<Course>) request.getAttribute("courses");
                %>
                <!DOCTYPE html>
                <html>

                <head>
                    <meta charset="UTF-8">
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
                            text-align: left;
                        }

                        th {
                            background-color: #eee;
                        }
                    </style>
                </head>

                <body>
                    <h2 style="text-align:center;">Courses List</h2>
                    <table>
                        <tr>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Credits</th>
                        </tr>
                        <% if (courses !=null && !courses.isEmpty()) { for (Course c : courses) { %>
                            <tr>
                                <td>
                                    <%= c.getName() %>
                                </td>
                                <td>
                                    <%= c.getDescription() %>
                                </td>
                                <td>
                                    <%= c.getCredits() %>
                                </td>
                            </tr>
                            <% } } else { %>
                                <tr>
                                    <td colspan="3" style="text-align:center;">No courses available</td>
                                </tr>
                                <% } %>
                    </table>
                </body>

                </html>