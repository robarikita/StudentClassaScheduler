<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; }
        .header { background: linear-gradient(135deg, #2c3e50, #4a6491); color: white;
                  padding: 30px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .nav-links { background: white; padding: 15px; border-radius: 8px; margin-bottom: 20px;
                     box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .nav-links a { display: inline-block; margin-right: 15px; padding: 10px 20px;
                       background: #3498db; color: white; text-decoration: none; border-radius: 5px;
                       transition: background 0.3s; }
        .nav-links a:hover { background: #2980b9; }
        .nav-links a.logout { background: #e74c3c; }
        .nav-links a.logout:hover { background: #c0392b; }
        .section { background: white; padding: 25px; border-radius: 8px; margin-bottom: 25px;
                   box-shadow: 0 2px 4px rgba(0,0,0,0.05); border-left: 5px solid #3498db; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th { background: #2c3e50; color: white; padding: 12px; text-align: left; }
        td { padding: 12px; border-bottom: 1px solid #eee; }
        tr:hover { background: #f9f9f9; }
        .btn { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px;
               transition: all 0.3s; }
        .btn-enroll { background: #27ae60; color: white; }
        .btn-enroll:hover { background: #219653; }
        .btn-drop { background: #e74c3c; color: white; }
        .btn-drop:hover { background: #c0392b; }
        .message { padding: 15px; margin: 15px 0; border-radius: 5px; border: 1px solid transparent; }
        .success { background: #d4edda; color: #155724; border-color: #c3e6cb; }
        .error { background: #f8d7da; color: #721c24; border-color: #f5c6cb; }
        .full { color: #e74c3c; font-weight: bold; padding: 4px 8px; background: #ffe6e6; border-radius: 3px; }
        .available { color: #27ae60; font-weight: bold; }
        .no-data { text-align: center; padding: 40px; color: #7f8c8d; font-style: italic; }
        .stats { display: flex; gap: 20px; margin-bottom: 20px; }
        .stat-card { flex: 1; background: white; padding: 20px; border-radius: 8px;
                     text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .stat-number { font-size: 36px; font-weight: bold; color: #3498db; }
        .stat-label { color: #7f8c8d; margin-top: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Welcome, ${student.firstName} ${student.lastName}!</h1>
            <p>Student ID: ${student.studentId} | Major: ${student.major} | Email: ${student.email}</p>
        </div>

        <!-- Display Messages -->
        <c:if test="${not empty successMessage}">
            <div class="message success">${successMessage}</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="message error">${errorMessage}</div>
        </c:if>

        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/student/DashboardServlet">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/ViewScheduleServlet">📅 My Schedule</a>
            <a href="${pageContext.request.contextPath}/student/BrowseCoursesServlet">🔍 Browse Courses</a>
            <a href="${pageContext.request.contextPath}/logout" class="logout">🚪 Logout</a>
        </div>

        <!-- Stats Cards -->
        <div class="stats">
            <div class="stat-card">
                <div class="stat-number">${currentSchedule.size()}</div>
                <div class="stat-label">Enrolled Courses</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${availableClasses.size()}</div>
                <div class="stat-label">Available Courses</div>
            </div>
        </div>

        <!-- Current Schedule Section -->
        <div class="section">
            <h2>📚 My Current Schedule</h2>
            <c:choose>
                <c:when test="${not empty currentSchedule}">
                    <table>
                        <thead>
                            <tr>
                                <th>Course</th>
                                <th>Instructor</th>
                                <th>Schedule</th>
                                <th>Room</th>
                                <th>Section</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${currentSchedule}">
                                <tr>
                                    <td>
                                        <strong>${item.courseCode}</strong><br>
                                        ${item.courseName}
                                    </td>
                                    <td>${item.instructorName}</td>
                                    <td>${item.dayOfWeek}<br>${item.startTime} - ${item.endTime}</td>
                                    <td>${item.building} ${item.roomNumber}</td>
                                    <td>${item.sectionNumber}</td>
                                    <td>
                                        <button class="btn btn-drop"
                                                onclick="dropClass(${item.studentScheduleId})">
                                            Drop
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="no-data">
                        <p>You are not enrolled in any classes yet.</p>
                        <a href="${pageContext.request.contextPath}/student/BrowseCoursesServlet"
                           style="display: inline-block; margin-top: 10px; padding: 10px 20px;
                                  background: #3498db; color: white; text-decoration: none; border-radius: 5px;">
                            Browse Available Courses
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Available Courses Section -->
        <div class="section">
            <h2>🎯 Available Courses</h2>
            <c:choose>
                <c:when test="${not empty availableClasses}">
                    <table>
                        <thead>
                            <tr>
                                <th>Course</th>
                                <th>Instructor</th>
                                <th>Schedule</th>
                                <th>Room</th>
                                <th>Seats</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cls" items="${availableClasses}">
                                <tr>
                                    <td>
                                        <strong>${cls.courseCode}</strong><br>
                                        ${cls.courseName}
                                    </td>
                                    <td>${cls.instructorName}</td>
                                    <td>${cls.dayOfWeek}<br>${cls.startTime} - ${cls.endTime}</td>
                                    <td>${cls.building} ${cls.roomNumber}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cls.currentEnrollment < cls.maxCapacity}">
                                                <span class="available">
                                                    ${cls.currentEnrollment}/${cls.maxCapacity}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="full">FULL</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cls.currentEnrollment < cls.maxCapacity}">
                                                <button class="btn btn-enroll"
                                                        onclick="enrollInClass(${cls.scheduleId})">
                                                    Enroll
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="full">Full</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="no-data">
                        <p>No available courses found for the current semester.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
    function enrollInClass(scheduleId) {
        if (confirm('Are you sure you want to enroll in this class?')) {
            window.location.href = '${pageContext.request.contextPath}/student/EnrollServlet?scheduleId=' + scheduleId;
        }
    }

    function dropClass(studentScheduleId) {
        if (confirm('Are you sure you want to drop this class?')) {
            window.location.href = '${pageContext.request.contextPath}/student/DropClassServlet?studentScheduleId=' + studentScheduleId;
        }
    }
    </script>
</body>
</html>