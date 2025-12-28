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
    // Check if user is logged in and is admin
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
    <title>Manage Time Slots - Admin Dashboard</title>
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

        .time-inputs {
            display: flex;
            gap: 1rem;
        }

        .time-inputs > div {
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

        tr:hover {
            background-color: #f8f9fa;
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
                <li><a href="${pageContext.request.contextPath}/admin/time-slots.jsp" class="active">Time Slots</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/classrooms.jsp">Classrooms</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/instructors.jsp">Instructors</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/semesters.jsp">Semesters</a></li>
                <li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
            </ul>
        </nav>
    </div>

    <div class="container">
        <div class="dashboard-header">
            <h1>Manage Time Slots</h1>
            <p>Create and manage time slots for class scheduling</p>
        </div>

        <!-- Success/Error Messages -->
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

        <!-- Add Time Slot Form -->
        <div class="form-section">
            <h3>Add New Time Slot</h3>
            <form action="${pageContext.request.contextPath}/admin/TimeSlotServlet" method="post">
                <div class="form-group">
                    <label for="slotName">Slot Name</label>
                    <input type="text" id="slotName" name="slotName"
                           placeholder="e.g., Morning Slot 1, Afternoon Slot 2" required>
                </div>

                <div class="form-group">
                    <label for="dayOfWeek">Day of Week</label>
                    <select id="dayOfWeek" name="dayOfWeek" required>
                        <option value="">Select Day</option>
                        <option value="Monday">Monday</option>
                        <option value="Tuesday">Tuesday</option>
                        <option value="Wednesday">Wednesday</option>
                        <option value="Thursday">Thursday</option>
                        <option value="Friday">Friday</option>
                        <option value="Saturday">Saturday</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Time Range</label>
                    <div class="time-inputs">
                        <div>
                            <label for="startTime">Start Time</label>
                            <input type="time" id="startTime" name="startTime" required>
                        </div>
                        <div>
                            <label for="endTime">End Time</label>
                            <input type="time" id="endTime" name="endTime" required>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn btn-success">Add Time Slot</button>
            </form>
        </div>

        <!-- Time Slots List -->
        <div class="table-container">
            <h3>Existing Time Slots</h3>

            <jsp:useBean id="timeSlotDAO" class="com.roba.scheduler.dao.TimeSlotDAO" />
            <%
                java.util.List<com.roba.scheduler.model.TimeSlot> timeSlots = timeSlotDAO.getAllTimeSlots();

                if (timeSlots.isEmpty()) {
            %>
            <div class="empty-state">
                <h4>No Time Slots Found</h4>
                <p>Create your first time slot using the form above.</p>
            </div>
            <%
                } else {
            %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Slot Name</th>
                        <th>Day</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                        <th>Duration</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (com.roba.scheduler.model.TimeSlot slot : timeSlots) {
                            // Calculate duration
                            java.sql.Time startTime = slot.getStartTime();
                            java.sql.Time endTime = slot.getEndTime();
                            long durationMs = endTime.getTime() - startTime.getTime();
                            long durationHours = durationMs / (1000 * 60 * 60);
                            long durationMinutes = (durationMs % (1000 * 60 * 60)) / (1000 * 60);
                    %>
                    <tr>
                        <td><%= slot.getSlotId() %></td>
                        <td><%= slot.getSlotName() %></td>
                        <td><%= slot.getDayOfWeek() %></td>
                        <td><%= startTime.toString().substring(0, 5) %></td>
                        <td><%= endTime.toString().substring(0, 5) %></td>
                        <td><%= durationHours %>h <%= durationMinutes %>m</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/TimeSlotServlet"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                <button type="submit" class="btn btn-danger"
                                        onclick="return confirm('Delete this time slot?')">
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
        // Set default time values
        document.getElementById('startTime').value = '08:00';
        document.getElementById('endTime').value = '09:30';

        // Validate end time is after start time
        document.querySelector('form').addEventListener('submit', function(e) {
            const startTime = document.getElementById('startTime').value;
            const endTime = document.getElementById('endTime').value;

            if (startTime >= endTime) {
                alert('End time must be after start time');
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