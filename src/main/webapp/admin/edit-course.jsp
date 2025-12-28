<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    if (session.getAttribute("user") == null || !"admin".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    // Get course from request attribute
    com.roba.scheduler.model.Course course = (com.roba.scheduler.model.Course) request.getAttribute("course");
    if (course == null) {
        response.sendRedirect(request.getContextPath() + "/admin/ListCoursesServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Course - Admin Dashboard</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }

        .header {
            background-color: #2c3e50;
            color: white;
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo { font-size: 1.5rem; font-weight: bold; }

        .nav-menu {
            display: flex;
            gap: 1rem;
            list-style: none;
        }

        .nav-menu a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .nav-menu a:hover { background-color: #34495e; }

        .container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .dashboard-header {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .btn {
            background-color: #3498db;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }

        .btn:hover { background-color: #2980b9; }
        .btn-success { background-color: #27ae60; }
        .btn-success:hover { background-color: #219653; }

        .form-section {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #333;
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .form-group textarea {
            height: 100px;
            resize: vertical;
        }

        .alert {
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1rem;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="logo">Admin Dashboard - Class Scheduler</div>
        <nav>
            <ul class="nav-menu">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/ListCoursesServlet">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
            </ul>
        </nav>
    </div>

    <div class="container">
        <div class="dashboard-header">
            <h1>Edit Course</h1>
            <p>Update course information</p>
        </div>

        <!-- Messages -->
        <%
            String message = request.getParameter("message");
            String error = request.getParameter("error");

            if (message != null) {
        %>
        <div class="alert alert-success">
            <%= java.net.URLDecoder.decode(message, "UTF-8") %>
        </div>
        <%
            }

            if (error != null) {
        %>
        <div class="alert alert-error">
            <%= java.net.URLDecoder.decode(error, "UTF-8") %>
        </div>
        <%
            }
        %>

        <div class="form-section">
            <form action="${pageContext.request.contextPath}/admin/EditCourseServlet" method="post">
                <input type="hidden" name="courseId" value="<%= course.getCourseId() %>">

                <div class="form-group">
                    <label for="courseCode">Course Code</label>
                    <input type="text" id="courseCode" name="courseCode"
                           value="<%= course.getCourseCode() %>" required>
                </div>

                <div class="form-group">
                    <label for="courseName">Course Name</label>
                    <input type="text" id="courseName" name="courseName"
                           value="<%= course.getCourseName() %>" required>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description"><%= course.getDescription() %></textarea>
                </div>

                <div class="form-group">
                    <label for="credits">Credits</label>
                    <input type="number" id="credits" name="credits"
                           value="<%= course.getCredits() %>" min="1" max="10" required>
                </div>

                <button type="submit" class="btn btn-success">Update Course</button>
                <a href="${pageContext.request.contextPath}/admin/ListCoursesServlet" class="btn">Cancel</a>
            </form>
        </div>
    </div>
</body>
</html>