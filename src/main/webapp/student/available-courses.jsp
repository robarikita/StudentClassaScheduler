<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Browse Courses</title>
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
        .filters { background: white; padding: 25px; border-radius: 8px; margin-bottom: 25px;
                   box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .filter-group { margin-bottom: 20px; }
        .filter-group label { display: block; margin-bottom: 8px; font-weight: bold; color: #2c3e50; }
        .filter-group select, .filter-group input { width: 100%; padding: 10px; border: 1px solid #ddd;
                                                    border-radius: 5px; font-size: 16px; }
        .btn-filter { background: #27ae60; color: white; padding: 12px 24px; border: none;
                      border-radius: 5px; cursor: pointer; font-size: 16px; margin-right: 10px;
                      transition: background 0.3s; }
        .btn-filter:hover { background: #219653; }
        .btn-clear { background: #e74c3c; }
        .btn-clear:hover { background: #c0392b; }
        .courses-table { width: 100%; border-collapse: collapse; margin-top: 20px;
                         background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .courses-table th { background: #2c3e50; color: white; padding: 15px; text-align: left; }
        .courses-table td { padding: 15px; border-bottom: 1px solid #eee; }
        .course-code { font-weight: bold; color: #2c3e50; font-size: 16px; }
        .seats-available { color: #27ae60; font-weight: bold; padding: 4px 8px; background: #e8f6ef; border-radius: 3px; }
        .seats-full { color: #e74c3c; font-weight: bold; padding: 4px 8px; background: #ffe6e6; border-radius: 3px; }
        .btn-enroll { background: #27ae60; color: white; padding: 8px 16px; border: none;
                      border-radius: 4px; cursor: pointer; font-size: 14px; transition: background 0.3s; }
        .btn-enroll:hover { background: #219653; }
        .btn-disabled { background: #95a5a6; color: white; padding: 8px 16px; border: none;
                        border-radius: 4px; cursor: not-allowed; }
        .no-courses { background: white; padding: 50px; text-align: center; border-radius: 8px;
                      color: #7f8c8d; font-style: italic; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .course-count { background: #3498db; color: white; padding: 5px 10px; border-radius: 20px;
                        font-size: 14px; margin-left: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/student/DashboardServlet" class="back-link">
            ← Back to Dashboard
        </a>

        <div class="header">
            <h1>🔍 Browse Available Courses</h1>
            <p>${student.firstName} ${student.lastName} | Student ID: ${student.studentId} | Semester: Fall 2024</p>
        </div>

        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/student/DashboardServlet">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/ViewScheduleServlet">📅 My Schedule</a>
            <a href="${pageContext.request.contextPath}/student/BrowseCoursesServlet">🔍 Browse Courses</a>
            <a href="${pageContext.request.contextPath}/logout" class="logout">🚪 Logout</a>
        </div>

        <!-- Filters Section -->
        <div class="filters">
            <h3>🔎 Filter Courses</h3>
            <form method="get" action="BrowseCoursesServlet">
                <div class="filter-group">
                    <label for="courseCode">Course Code:</label>
                    <select id="courseCode" name="courseCode">
                        <option value="">All Courses</option>
                        <c:forEach var="course" items="${allCourses}">
                            <option value="${course.courseCode}">${course.courseCode} - ${course.courseName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="filter-group">
                    <label for="day">Day of Week:</label>
                    <select id="day" name="day">
                        <option value="">All Days</option>
                        <option value="Monday">Monday</option>
                        <option value="Tuesday">Tuesday</option>
                        <option value="Wednesday">Wednesday</option>
                        <option value="Thursday">Thursday</option>
                        <option value="Friday">Friday</option>
                        <option value="Saturday">Saturday</option>
                    </select>
                </div>

                <button type="submit" class="btn-filter">Apply Filters</button>
                <button type="button" class="btn-filter btn-clear" onclick="window.location.href='BrowseCoursesServlet'">
                    Clear Filters
                </button>
            </form>
        </div>

        <!-- Courses List -->
        <h2>Available Classes <span class="course-count">${availableClasses.size()}</span></h2>
        <c:choose>
            <c:when test="${not empty availableClasses}">
                <table class="courses-table">
                    <thead>
                        <tr>
                            <th>Course Details</th>
                            <th>Schedule</th>
                            <th>Instructor</th>
                            <th>Room</th>
                            <th>Seats</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cls" items="${availableClasses}">
                            <tr>
                                <td>
                                    <div class="course-code">${cls.courseCode}</div>
                                    <div><strong>${cls.courseName}</strong></div>
                                    <div>Section: ${cls.sectionNumber}</div>
                                    <div style="color: #666; font-size: 14px; margin-top: 5px;">${cls.description}</div>
                                </td>
                                <td>
                                    <div><strong>${cls.dayOfWeek}</strong></div>
                                    <div>${cls.startTime} - ${cls.endTime}</div>
                                </td>
                                <td>${cls.instructorName}</td>
                                <td>${cls.building} ${cls.roomNumber}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cls.currentEnrollment < cls.maxCapacity}">
                                            <span class="seats-available">
                                                ${cls.currentEnrollment}/${cls.maxCapacity}
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="seats-full">FULL</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cls.currentEnrollment < cls.maxCapacity}">
                                            <button class="btn-enroll"
                                                    onclick="enrollInClass(${cls.scheduleId})">
                                                Enroll
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn-disabled" disabled>Full</button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-courses">
                    <h3>No courses available</h3>
                    <p>There are no available courses matching your criteria.</p>
                    <p>Try adjusting your filters or check back later.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
    function enrollInClass(scheduleId) {
        if (confirm('Are you sure you want to enroll in this class?')) {
            window.location.href = '${pageContext.request.contextPath}/student/EnrollServlet?scheduleId=' + scheduleId;
        }
    }
    </script>
</body>
</html>