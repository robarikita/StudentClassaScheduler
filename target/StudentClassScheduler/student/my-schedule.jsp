<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Schedule</title>
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
        .back-link { display: inline-block; margin-bottom: 20px; padding: 10px 20px;
                     background: #95a5a6; color: white; text-decoration: none; border-radius: 5px; }
        .back-link:hover { background: #7f8c8d; }
        .day-section { margin-bottom: 25px; }
        .day-header { background: #3498db; color: white; padding: 15px 20px; border-radius: 8px 8px 0 0;
                      font-size: 18px; font-weight: bold; }
        .class-card { background: white; padding: 20px; margin-bottom: 10px; border-radius: 0 0 8px 8px;
                      border-left: 5px solid #27ae60; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .class-time { font-weight: bold; color: #2c3e50; font-size: 16px; margin-bottom: 10px;
                      display: flex; align-items: center; }
        .class-time::before { content: "🕒"; margin-right: 8px; }
        .no-classes { background: white; padding: 40px; text-align: center; border-radius: 8px;
                      color: #7f8c8d; font-style: italic; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .schedule-table { width: 100%; border-collapse: collapse; margin-top: 25px;
                          background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .schedule-table th { background: #2c3e50; color: white; padding: 15px; text-align: left; }
        .schedule-table td { padding: 15px; border-bottom: 1px solid #eee; }
        .schedule-table tr:last-child td { border-bottom: none; }
    </style>
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/student/DashboardServlet" class="back-link">
            ← Back to Dashboard
        </a>

        <div class="header">
            <h1>📅 My Class Schedule</h1>
            <p>${student.firstName} ${student.lastName} | Student ID: ${student.studentId} | Major: ${student.major}</p>
        </div>

        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/student/DashboardServlet">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/ViewScheduleServlet">📅 My Schedule</a>
            <a href="${pageContext.request.contextPath}/student/BrowseCoursesServlet">🔍 Browse Courses</a>
            <a href="${pageContext.request.contextPath}/logout" class="logout">🚪 Logout</a>
        </div>

        <!-- Weekly Schedule View -->
        <c:choose>
            <c:when test="${not empty fullSchedule}">
                <c:forEach var="day" items="${scheduleByDay}">
                    <c:if test="${not empty day.value}">
                        <div class="day-section">
                            <div class="day-header">
                                <h2>${day.key}</h2>
                            </div>
                            <c:forEach var="cls" items="${day.value}">
                                <div class="class-card">
                                    <div class="class-time">
                                        ${cls.startTime} - ${cls.endTime}
                                    </div>
                                    <h3>${cls.courseCode} - ${cls.courseName}</h3>
                                    <p><strong>Instructor:</strong> ${cls.instructorName}</p>
                                    <p><strong>Room:</strong> ${cls.building} ${cls.roomNumber}</p>
                                    <p><strong>Section:</strong> ${cls.sectionNumber}</p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                </c:forEach>

                <!-- Table View -->
                <h2>📋 Schedule List</h2>
                <table class="schedule-table">
                    <thead>
                        <tr>
                            <th>Course</th>
                            <th>Instructor</th>
                            <th>Day & Time</th>
                            <th>Room</th>
                            <th>Section</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cls" items="${fullSchedule}">
                            <tr>
                                <td>
                                    <strong>${cls.courseCode}</strong><br>
                                    ${cls.courseName}
                                </td>
                                <td>${cls.instructorName}</td>
                                <td>
                                    <strong>${cls.dayOfWeek}</strong><br>
                                    ${cls.startTime} - ${cls.endTime}
                                </td>
                                <td>${cls.building} ${cls.roomNumber}</td>
                                <td>${cls.sectionNumber}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-classes">
                    <h2>No Classes Enrolled</h2>
                    <p>You are not enrolled in any classes for the current semester.</p>
                    <a href="${pageContext.request.contextPath}/student/BrowseCoursesServlet"
                       style="display: inline-block; margin-top: 15px; padding: 12px 24px;
                              background: #3498db; color: white; text-decoration: none; border-radius: 5px;">
                        Browse Available Courses
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>