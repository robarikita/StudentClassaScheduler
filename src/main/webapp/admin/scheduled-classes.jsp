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
    <title>Schedule Classes - Admin Dashboard</title>
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
            max-width: 1400px;
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
        .btn-warning { background-color: #f39c12; }
        .btn-warning:hover { background-color: #d68910; }

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

        .form-row {
            display: flex;
            gap: 1rem;
        }

        .form-row > div {
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

        .enrollment-info {
            font-size: 0.9rem;
            color: #666;
        }

        .full-class { color: #e74c3c; font-weight: bold; }
        .available-class { color: #27ae60; }

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

        .alert-warning {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeaa7;
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #666;
        }

        .filter-section {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .filter-form {
            display: flex;
            gap: 1rem;
            align-items: flex-end;
        }

        .conflict-check {
            background-color: #fff3cd;
            padding: 1rem;
            border-radius: 6px;
            margin-top: 1rem;
            border-left: 4px solid #f39c12;
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
                <li><a href="${pageContext.request.contextPath}/admin/semesters.jsp">Semesters</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/scheduled-classes.jsp" class="active">Schedule Classes</a></li>
                <li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
            </ul>
        </nav>
    </div>

    <div class="container">
        <div class="dashboard-header">
            <h1>Schedule Classes</h1>
            <p>Create and manage scheduled class instances</p>
        </div>

        <!-- Messages -->
        <%
            String message = request.getParameter("message");
            String error = request.getParameter("error");
            String warning = request.getParameter("warning");

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

            if (warning != null) {
        %>
        <div class="alert alert-warning">
            <%= java.net.URLDecoder.decode(warning, "UTF-8") %>
        </div>
        <%
            }
        %>

        <!-- Add Scheduled Class Form -->
        <div class="form-section">
            <h3>Schedule New Class</h3>
            <form action="${pageContext.request.contextPath}/admin/ScheduleClassServlet" method="post" id="scheduleForm">

                <div class="form-row">
                    <div class="form-group">
                        <label for="semesterId">Semester</label>
                        <select id="semesterId" name="semesterId" required>
                            <option value="">Select Semester</option>
                            <jsp:useBean id="semesterDAO" class="com.roba.scheduler.dao.SemesterDAO" />
                            <%
                                java.util.List<com.roba.scheduler.model.Semester> semesters = semesterDAO.getAllSemesters();
                                for (com.roba.scheduler.model.Semester semester : semesters) {
                            %>
                            <option value="<%= semester.getSemesterId() %>">
                                <%= semester.getSemesterName() %> (<%= semester.getSemesterCode() %>)
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="courseId">Course</label>
                        <select id="courseId" name="courseId" required>
                            <option value="">Select Course</option>
                            <jsp:useBean id="courseDAO" class="com.roba.scheduler.dao.CourseDAO" />
                            <%
                                java.util.List<com.roba.scheduler.model.Course> courses = courseDAO.getAllCourses();
                                for (com.roba.scheduler.model.Course course : courses) {
                            %>
                            <option value="<%= course.getCourseId() %>">
                                <%= course.getCourseCode() %> - <%= course.getCourseName() %> (<%= course.getCredits() %> credits)
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="sectionNumber">Section Number</label>
                        <input type="text" id="sectionNumber" name="sectionNumber"
                               placeholder="e.g., 001, 002, L01" value="001" required>
                    </div>

                    <div class="form-group">
                        <label for="maxCapacity">Maximum Capacity</label>
                        <input type="number" id="maxCapacity" name="maxCapacity"
                               min="1" max="500" value="30" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="instructorId">Instructor</label>
                        <select id="instructorId" name="instructorId">
                            <option value="">Select Instructor (Optional)</option>
                            <jsp:useBean id="instructorDAO" class="com.roba.scheduler.dao.InstructorDAO" />
                            <%
                                java.util.List<com.roba.scheduler.model.Instructor> instructors = instructorDAO.getAllInstructors();
                                for (com.roba.scheduler.model.Instructor instructor : instructors) {
                            %>
                            <option value="<%= instructor.getInstructorId() %>">
                                <%= instructor.getFirstName() %> <%= instructor.getLastName() %> - <%= instructor.getDepartment() %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="slotId">Time Slot</label>
                        <select id="slotId" name="slotId" required>
                            <option value="">Select Time Slot</option>
                            <jsp:useBean id="timeSlotDAO" class="com.roba.scheduler.dao.TimeSlotDAO" />
                            <%
                                java.util.List<com.roba.scheduler.model.TimeSlot> timeSlots = timeSlotDAO.getAllTimeSlots();
                                for (com.roba.scheduler.model.TimeSlot slot : timeSlots) {
                            %>
                            <option value="<%= slot.getSlotId() %>">
                                <%= slot.getFullDisplay() %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="roomId">Classroom</label>
                        <select id="roomId" name="roomId">
                            <option value="">Select Classroom (Optional)</option>
                            <jsp:useBean id="classroomDAO" class="com.roba.scheduler.dao.ClassroomDAO" />
                            <%
                                java.util.List<com.roba.scheduler.model.Classroom> classrooms = classroomDAO.getAllClassrooms();
                                for (com.roba.scheduler.model.Classroom room : classrooms) {
                            %>
                            <option value="<%= room.getRoomId() %>" data-capacity="<%= room.getCapacity() %>">
                                <%= room.getRoomInfo() %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                </div>

                <!-- Conflict Check Section -->
                <div id="conflictCheck" class="conflict-check" style="display: none;">
                    <h4>⚠️ Potential Conflicts Detected</h4>
                    <div id="conflictDetails"></div>
                </div>

                <button type="submit" class="btn btn-success" id="submitBtn">Schedule Class</button>
                <button type="button" class="btn" onclick="checkConflicts()">Check for Conflicts</button>
            </form>
        </div>

        <!-- Filter Section -->
        <div class="filter-section">
            <h3>Filter Scheduled Classes</h3>
            <form action="scheduled-classes.jsp" method="get" class="filter-form">
                <div class="form-group" style="flex: 1;">
                    <label for="filterSemester">By Semester</label>
                    <select id="filterSemester" name="semesterId">
                        <option value="">All Semesters</option>
                        <%
                            for (com.roba.scheduler.model.Semester semester : semesters) {
                                String selected = request.getParameter("semesterId") != null &&
                                                 request.getParameter("semesterId").equals(String.valueOf(semester.getSemesterId())) ?
                                                 "selected" : "";
                        %>
                        <option value="<%= semester.getSemesterId() %>" <%= selected %>>
                            <%= semester.getSemesterName() %>
                        </option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div class="form-group" style="flex: 1;">
                    <label for="filterCourse">By Course</label>
                    <select id="filterCourse" name="courseId">
                        <option value="">All Courses</option>
                        <%
                            for (com.roba.scheduler.model.Course course : courses) {
                                String selected = request.getParameter("courseId") != null &&
                                                 request.getParameter("courseId").equals(String.valueOf(course.getCourseId())) ?
                                                 "selected" : "";
                        %>
                        <option value="<%= course.getCourseId() %>" <%= selected %>>
                            <%= course.getCourseCode() %>
                        </option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <button type="submit" class="btn">Filter</button>
                <a href="scheduled-classes.jsp" class="btn">Clear Filter</a>
            </form>
        </div>

        <!-- Scheduled Classes List -->
        <div class="table-container">
            <h3>Scheduled Classes</h3>

            <jsp:useBean id="scheduledClassDAO" class="com.roba.scheduler.dao.ScheduledClassDAO" />
            <%
                java.util.List<com.roba.scheduler.model.ScheduledClass> scheduledClasses;

                String filterSemesterId = request.getParameter("semesterId");
                String filterCourseId = request.getParameter("courseId");

                if (filterSemesterId != null && !filterSemesterId.isEmpty()) {
                    int semesterId = Integer.parseInt(filterSemesterId);
                    scheduledClasses = scheduledClassDAO.getScheduledClassesBySemester(semesterId);
                } else {
                    scheduledClasses = scheduledClassDAO.getAllScheduledClasses();
                }

                // Additional filtering by course if specified
                if (filterCourseId != null && !filterCourseId.isEmpty()) {
                    int courseId = Integer.parseInt(filterCourseId);
                    java.util.List<com.roba.scheduler.model.ScheduledClass> filtered = new java.util.ArrayList<>();
                    for (com.roba.scheduler.model.ScheduledClass sc : scheduledClasses) {
                        if (sc.getCourse().getCourseId() == courseId) {
                            filtered.add(sc);
                        }
                    }
                    scheduledClasses = filtered;
                }

                if (scheduledClasses.isEmpty()) {
            %>
            <div class="empty-state">
                <h4>No Scheduled Classes Found</h4>
                <p>Schedule your first class using the form above.</p>
            </div>
            <%
                } else {
            %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Course</th>
                        <th>Section</th>
                        <th>Semester</th>
                        <th>Time</th>
                        <th>Instructor</th>
                        <th>Classroom</th>
                        <th>Enrollment</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (com.roba.scheduler.model.ScheduledClass sc : scheduledClasses) {
                            boolean isFull = sc.getCurrentEnrollment() >= sc.getMaxCapacity();
                    %>
                    <tr>
                        <td><%= sc.getScheduleId() %></td>
                        <td>
                            <strong><%= sc.getCourse().getCourseCode() %></strong><br>
                            <%= sc.getCourse().getCourseName() %>
                        </td>
                        <td><%= sc.getSectionNumber() %></td>
                        <td><%= sc.getSemester().getSemesterName() %></td>
                        <td>
                            <%= sc.getTimeSlot().getDayOfWeek().substring(0, 3) %><br>
                            <%= sc.getTimeSlot().getStartTime().toString().substring(0, 5) %> -
                            <%= sc.getTimeSlot().getEndTime().toString().substring(0, 5) %>
                        </td>
                        <td>
                            <% if (sc.getInstructor() != null) { %>
                                <%= sc.getInstructor().getFirstName() %> <%= sc.getInstructor().getLastName() %>
                            <% } else { %>
                                <em>TBA</em>
                            <% } %>
                        </td>
                        <td>
                            <% if (sc.getClassroom() != null) { %>
                                <%= sc.getClassroom().getBuilding() %> <%= sc.getClassroom().getRoomNumber() %>
                            <% } else { %>
                                <em>TBA</em>
                            <% } %>
                        </td>
                        <td>
                            <div class="enrollment-info <%= isFull ? "full-class" : "available-class" %>">
                                <%= sc.getCurrentEnrollment() %> / <%= sc.getMaxCapacity() %>
                                <% if (isFull) { %>
                                    <br><span class="full-class">FULL</span>
                                <% } else { %>
                                    <br><span class="available-class"><%= sc.getAvailableSeats() %> seats available</span>
                                <% } %>
                            </div>
                        </td>
                        <td>
                            <% if (sc.getIsActive()) { %>
                                <span style="color: #27ae60; font-weight: bold;">Active</span>
                            <% } else { %>
                                <span style="color: #e74c3c;">Inactive</span>
                            <% } %>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/edit-schedule.jsp?scheduleId=<%= sc.getScheduleId() %>"
                               class="btn">Edit</a>
                            <form action="${pageContext.request.contextPath}/admin/ScheduleClassServlet"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="scheduleId" value="<%= sc.getScheduleId() %>">
                                <button type="submit" class="btn btn-warning">
                                    <%= sc.getIsActive() ? "Deactivate" : "Activate" %>
                                </button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/ScheduleClassServlet"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="scheduleId" value="<%= sc.getScheduleId() %>">
                                <button type="submit" class="btn btn-danger"
                                        onclick="return confirm('Delete this scheduled class?')">
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
        // Update max capacity based on selected classroom
        document.getElementById('roomId').addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            if (selectedOption.value && selectedOption.dataset.capacity) {
                document.getElementById('maxCapacity').value = selectedOption.dataset.capacity;
                document.getElementById('maxCapacity').max = selectedOption.dataset.capacity;
            }
        });

        // Check for conflicts
        function checkConflicts() {
            const semesterId = document.getElementById('semesterId').value;
            const instructorId = document.getElementById('instructorId').value;
            const roomId = document.getElementById('roomId').value;
            const slotId = document.getElementById('slotId').value;

            if (!semesterId || !slotId) {
                alert('Please select semester and time slot first');
                return;
            }

            const conflictCheck = document.getElementById('conflictCheck');
            const conflictDetails = document.getElementById('conflictDetails');
            const submitBtn = document.getElementById('submitBtn');

            // Clear previous results
            conflictCheck.style.display = 'none';
            conflictDetails.innerHTML = '';
            submitBtn.disabled = false;

            // Simulate conflict check (in real app, make AJAX call)
            let conflicts = [];

            if (instructorId) {
                conflicts.push('Instructor might have conflict at this time');
            }

            if (roomId) {
                conflicts.push('Classroom might be occupied at this time');
            }

            if (conflicts.length > 0) {
                conflictCheck.style.display = 'block';
                conflictDetails.innerHTML = '<ul>' +
                    conflicts.map(c => `<li>${c}</li>`).join('') +
                    '</ul>';
                conflictDetails.innerHTML += '<p><small>You can still schedule the class, but conflicts should be resolved.</small></p>';
            } else {
                conflictCheck.style.display = 'block';
                conflictDetails.innerHTML = '<p style="color: #27ae60;">✅ No conflicts detected</p>';
            }
        }

        // Form submission with confirmation
        document.getElementById('scheduleForm').addEventListener('submit', function(e) {
            const conflictCheck = document.getElementById('conflictCheck');
            if (conflictCheck.style.display === 'block') {
                if (!confirm('Are you sure you want to schedule this class? Check for conflicts first.')) {
                    e.preventDefault();
                }
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