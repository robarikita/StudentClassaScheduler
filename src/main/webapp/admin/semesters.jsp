<%@ page import="com.roba.scheduler.model.User" %>
<%
    // Debug: Print session info
    System.out.println("=== Checking session for time-slots.jsp ===");
    System.out.println("Session ID: " + session.getId());

    User user = (User) session.getAttribute("user");
    if (user == null) {
        System.out.println("USER IS NULL - redirecting to login");
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    System.out.println("User found: " + user.getUsername());
    System.out.println("User role: " + user.getRole());

    if (!"admin".equals(user.getRole())) {
        System.out.println("User is not admin - redirecting");
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    System.out.println("Access granted to admin: " + user.getUsername());
%>



<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    if (session.getAttribute("user") == null || !"admin".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Semesters - Admin Dashboard</title>
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
        .nav-menu a.active { background-color: #34495e; }

        .container {
            max-width: 1200px;
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
        .btn-danger { background-color: #e74c3c; }
        .btn-danger:hover { background-color: #c0392b; }

        .form-section {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
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
        .form-group select {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .date-inputs {
            display: flex;
            gap: 1rem;
        }

        .date-inputs > div {
            flex: 1;
        }

        .table-container {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        th, td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        th {
            background-color: #2c3e50;
            color: white;
        }

        tr:hover { background-color: #f8f9fa; }

        .status-active {
            background-color: #d4edda;
            color: #155724;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
            font-weight: bold;
        }

        .status-inactive {
            background-color: #f8d7da;
            color: #721c24;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
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

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #666;
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
                <li><a href="${pageContext.request.contextPath}/admin/time-slots.jsp">Time Slots</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/classrooms.jsp">Classrooms</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/instructors.jsp">Instructors</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/semesters.jsp" class="active">Semesters</a></li>
                <li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
            </ul>
        </nav>
    </div>

    <div class="container">
        <div class="dashboard-header">
            <h1>Manage Semesters</h1>
            <p>Create and manage academic semesters</p>
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

        <!-- Add Semester Form -->
        <div class="form-section">
            <h3>Add New Semester</h3>
            <form action="${pageContext.request.contextPath}/admin/SemesterServlet" method="post">
                <div class="form-group">
                    <label for="semesterCode">Semester Code</label>
                    <input type="text" id="semesterCode" name="semesterCode"
                           placeholder="e.g., FALL2024, SPRING2025" required>
                    <small>Unique code for the semester</small>
                </div>

                <div class="form-group">
                    <label for="semesterName">Semester Name</label>
                    <input type="text" id="semesterName" name="semesterName"
                           placeholder="e.g., Fall 2024, Spring 2025" required>
                </div>

                <div class="form-group">
                    <label>Semester Dates</label>
                    <div class="date-inputs">
                        <div>
                            <label for="startDate">Start Date</label>
                            <input type="date" id="startDate" name="startDate" required>
                        </div>
                        <div>
                            <label for="endDate">End Date</label>
                            <input type="date" id="endDate" name="endDate" required>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label>Registration Period</label>
                    <div class="date-inputs">
                        <div>
                            <label for="registrationStart">Registration Start</label>
                            <input type="date" id="registrationStart" name="registrationStart" required>
                        </div>
                        <div>
                            <label for="registrationEnd">Registration End</label>
                            <input type="date" id="registrationEnd" name="registrationEnd" required>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label>
                        <input type="checkbox" name="isActive" value="true">
                        Set as Active Semester
                    </label>
                    <small>Only one semester can be active at a time</small>
                </div>

                <button type="submit" class="btn btn-success">Add Semester</button>
            </form>
        </div>

        <!-- Semesters List -->
        <div class="table-container">
            <h3>Existing Semesters</h3>

            <jsp:useBean id="semesterDAO" class="com.roba.scheduler.dao.SemesterDAO" />
            <%
                java.util.List<com.roba.scheduler.model.Semester> semesters = semesterDAO.getAllSemesters();

                if (semesters.isEmpty()) {
            %>
            <div class="empty-state">
                <h4>No Semesters Found</h4>
                <p>Add your first semester using the form above.</p>
            </div>
            <%
                } else {
            %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Registration Period</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (com.roba.scheduler.model.Semester semester : semesters) {
                    %>
                    <tr>
                        <td><%= semester.getSemesterId() %></td>
                        <td><%= semester.getSemesterCode() %></td>
                        <td><%= semester.getSemesterName() %></td>
                        <td><%= semester.getStartDate() %></td>
                        <td><%= semester.getEndDate() %></td>
                        <td>
                            <%= semester.getRegistrationStart() %> to<br>
                            <%= semester.getRegistrationEnd() %>
                        </td>
                        <td>
                            <% if (semester.getIsActive()) { %>
                                <span class="status-active">Active</span>
                            <% } else { %>
                                <span class="status-inactive">Inactive</span>
                            <% } %>
                        </td>
                        <td>
                            <% if (!semester.getIsActive()) { %>
                            <form action="${pageContext.request.contextPath}/admin/SemesterServlet"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="activate">
                                <input type="hidden" name="semesterId" value="<%= semester.getSemesterId() %>">
                                <button type="submit" class="btn btn-success">
                                    Set Active
                                </button>
                            </form>
                            <% } %>

                            <form action="${pageContext.request.contextPath}/admin/SemesterServlet"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="semesterId" value="<%= semester.getSemesterId() %>">
                                <button type="submit" class="btn btn-danger"
                                        onclick="return confirm('Delete this semester? This will also delete all scheduled classes for this semester.')">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <%
                }
            %>
        </div>
    </div>

    <script>
        // Set default dates
        const today = new Date();
        const nextMonth = new Date(today.getFullYear(), today.getMonth() + 1, 1);
        const endOfNextMonth = new Date(today.getFullYear(), today.getMonth() + 4, 0);

        document.getElementById('startDate').value = nextMonth.toISOString().split('T')[0];
        document.getElementById('endDate').value = endOfNextMonth.toISOString().split('T')[0];

        // Set registration dates (2 weeks before start)
        const twoWeeksBefore = new Date(nextMonth);
        twoWeeksBefore.setDate(twoWeeksBefore.getDate() - 14);
        const registrationEnd = new Date(nextMonth);
        registrationEnd.setDate(registrationEnd.getDate() + 7);

        document.getElementById('registrationStart').value = twoWeeksBefore.toISOString().split('T')[0];
        document.getElementById('registrationEnd').value = registrationEnd.toISOString().split('T')[0];

        // Validate dates
        document.querySelector('form').addEventListener('submit', function(e) {
            const startDate = new Date(document.getElementById('startDate').value);
            const endDate = new Date(document.getElementById('endDate').value);
            const regStart = new Date(document.getElementById('registrationStart').value);
            const regEnd = new Date(document.getElementById('registrationEnd').value);

            if (endDate <= startDate) {
                alert('End date must be after start date');
                e.preventDefault();
            }

            if (regEnd <= regStart) {
                alert('Registration end must be after registration start');
                e.preventDefault();
            }

            if (regStart >= startDate) {
                alert('Registration should start before semester start date');
                e.preventDefault();
            }
        });

        // Clear messages after 5 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                alert.style.opacity = '0';
                alert.style.transition = 'opacity 0.5s';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);
    </script>
</body>
</html>