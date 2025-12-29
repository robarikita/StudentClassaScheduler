<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in and is admin
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String username = (String) session.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
        }

        .header {
            background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
            color: white;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            position: sticky;
            top: 0;
            z-index: 1000;
        }

        .logo {
            font-size: 1.8rem;
            font-weight: bold;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .nav-menu {
            display: flex;
            gap: 0.5rem;
            list-style: none;
        }

        .nav-menu a {
            color: white;
            text-decoration: none;
            padding: 0.75rem 1.25rem;
            border-radius: 8px;
            transition: all 0.3s;
            font-weight: 500;
        }

        .nav-menu a:hover {
            background: rgba(255,255,255,0.1);
            transform: translateY(-2px);
        }

        .nav-menu a.active {
            background: #3498db;
            box-shadow: 0 4px 8px rgba(52, 152, 219, 0.3);
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .logout-btn {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 0.6rem 1.2rem;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            font-weight: bold;
            transition: all 0.3s;
        }

        .logout-btn:hover {
            background: #c0392b;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3);
        }

        .container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .welcome-card {
            background: white;
            padding: 2.5rem;
            border-radius: 20px;
            margin-bottom: 2.5rem;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
            border: 1px solid rgba(0,0,0,0.05);
        }

        .welcome-card h1 {
            color: #2c3e50;
            margin-bottom: 0.5rem;
            font-size: 2.5rem;
        }

        .welcome-card p {
            color: #7f8c8d;
            font-size: 1.1rem;
            line-height: 1.6;
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 2rem;
        }

        .feature-card {
            background: white;
            padding: 2rem;
            border-radius: 16px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.08);
            transition: all 0.4s;
            border: 1px solid rgba(0,0,0,0.05);
            position: relative;
            overflow: hidden;
        }

        .feature-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.15);
        }

        .feature-card h3 {
            color: #2c3e50;
            margin-bottom: 1rem;
            font-size: 1.4rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }

        .feature-card p {
            color: #666;
            margin-bottom: 1.5rem;
            line-height: 1.6;
        }

        .btn {
            display: inline-block;
            padding: 0.8rem 1.5rem;
            border-radius: 8px;
            text-decoration: none;
            font-weight: bold;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
        }

        .btn-primary {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
        }

        .btn-primary:hover {
            background: linear-gradient(135deg, #2980b9 0%, #1f6394 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
        }

        .btn-success {
            background: linear-gradient(135deg, #27ae60 0%, #219653 100%);
            color: white;
        }

        .btn-success:hover {
            background: linear-gradient(135deg, #219653 0%, #1e8449 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(39, 174, 96, 0.4);
        }

        .btn-purple {
            background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
            color: white;
        }

        .btn-purple:hover {
            background: linear-gradient(135deg, #8e44ad 0%, #7d3c98 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(155, 89, 182, 0.4);
        }

        .btn-orange {
            background: linear-gradient(135deg, #e67e22 0%, #d35400 100%);
            color: white;
        }

        .btn-orange:hover {
            background: linear-gradient(135deg, #d35400 0%, #ba4a00 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(230, 126, 34, 0.4);
        }

        .btn-red {
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            color: white;
        }

        .btn-red:hover {
            background: linear-gradient(135deg, #c0392b 0%, #a93226 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(231, 76, 60, 0.4);
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 1.5rem;
            margin-top: 2rem;
        }

        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            text-align: center;
        }

        .stat-card h4 {
            color: #7f8c8d;
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .stat-card .number {
            font-size: 2rem;
            font-weight: bold;
            color: #2c3e50;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="logo">🏫 Student Class Scheduler</div>

        <nav>
            <ul class="nav-menu">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/ListCoursesServlet">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/time-slots.jsp">Time Slots</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/classrooms.jsp">Classrooms</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/instructors.jsp">Instructors</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/semesters.jsp">Semesters</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/scheduled-classes.jsp">Schedule Classes</a></li>
            </ul>
        </nav>

        <div class="user-info">
            <span>Welcome, <strong><%= username %></strong> (Admin)</span>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="container">
        <div class="welcome-card">
            <h1>👋 Welcome to Admin Dashboard</h1>
            <p>Manage all aspects of the Student Class Scheduler system from this central dashboard.
               You can schedule classes, manage courses, instructors, and monitor system activities.</p>
        </div>

        <h2 style="color: #2c3e50; margin-bottom: 1.5rem; font-size: 1.8rem;">📊 Management Panels</h2>

        <div class="dashboard-grid">
            <!-- Phase 5 Features -->
            <div class="feature-card">
                <h3>📅 Time Slots</h3>
                <p>Create and manage time slots for class scheduling. Define days of week, start and end times for classes.</p>
                <a href="time-slots.jsp" class="btn btn-primary">Manage Time Slots</a>
            </div>

            <div class="feature-card">
                <h3>🏫 Classrooms</h3>
                <p>Add and manage classrooms with capacity, facilities (projector, computers), and availability status.</p>
                <a href="classrooms.jsp" class="btn btn-success">Manage Classrooms</a>
            </div>

            <div class="feature-card">
                <h3>👨‍🏫 Instructors</h3>
                <p>Manage instructor profiles, departments, office numbers, and course assignments.</p>
                <a href="instructors.jsp" class="btn btn-purple">Manage Instructors</a>
            </div>

            <div class="feature-card">
                <h3>📚 Semesters</h3>
                <p>Create and manage semesters with registration dates, active periods, and academic schedules.</p>
                <a href="semesters.jsp" class="btn btn-orange">Manage Semesters</a>
            </div>

            <div class="feature-card">
                <h3>📋 Schedule Classes</h3>
                <p>Schedule classes by assigning courses to time slots, rooms, and instructors with conflict detection.</p>
                <a href="scheduled-classes.jsp" class="btn btn-red">Schedule Classes</a>
            </div>

            <!-- Phase 4 Features -->
            <div class="feature-card">
                <h3>📖 Course Management</h3>
                <p>Manage the course catalog - add, edit, and delete courses with codes, names, descriptions, and credits.</p>
                <a href="${pageContext.request.contextPath}/admin/ListCoursesServlet" class="btn btn-primary">Manage Courses</a>
            </div>

            <!-- Student Management -->
            <div class="feature-card">
                <h3>👨‍🎓 Student Management</h3>
                <p>View and manage student profiles, enrollments, and academic records.</p>
                <a href="#" class="btn btn-success">Manage Students</a>
            </div>

            <!-- Reports -->
            <div class="feature-card">
                <h3>📈 Reports & Analytics</h3>
                <p>Generate system reports, view statistics, and monitor class enrollment trends.</p>
                <a href="#" class="btn btn-purple">View Reports</a>
            </div>
        </div>

        <!-- Quick Stats Section -->
        <div style="margin-top: 3rem;">
            <h2 style="color: #2c3e50; margin-bottom: 1.5rem; font-size: 1.8rem;">📈 System Overview</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <h4>Total Courses</h4>
                    <div class="number">12</div>
                </div>
                <div class="stat-card">
                    <h4>Active Classes</h4>
                    <div class="number">8</div>
                </div>
                <div class="stat-card">
                    <h4>Registered Students</h4>
                    <div class="number">45</div>
                </div>
                <div class="stat-card">
                    <h4>Available Rooms</h4>
                    <div class="number">15</div>
                </div>
            </div>
        </div>
    </div>
<!-- ID Management Panel -->
<div class="feature-card" style="grid-column: span 2;">
    <h3>🔧 ID Management Tools</h3>
    <p>Manage database ID sequences and reuse deleted IDs</p>

    <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-top: 15px;">

        <!-- Fix All IDs -->
        <form action="${pageContext.request.contextPath}/admin/IDManagementServlet" method="post">
            <input type="hidden" name="action" value="fixAll">
            <button type="submit" class="btn btn-primary"
                    onclick="return confirm('Fix auto-increment for all tables?')">
                Fix All ID Sequences
            </button>
        </form>

        <!-- Compact All IDs -->
        <form action="${pageContext.request.contextPath}/admin/IDManagementServlet" method="post">
            <input type="hidden" name="action" value="compactAll">
            <button type="submit" class="btn btn-warning"
                    onclick="return confirm('Compact all IDs (reorganize sequentially)? This may take time.')">
                Compact All IDs
            </button>
        </form>

        <!-- Individual Table Management -->
        <select id="tableSelect" style="grid-column: span 2; padding: 8px;">
            <option value="">Select Table to Manage</option>
            <option value="classrooms">Classrooms</option>
            <option value="instructors">Instructors</option>
            <option value="courses">Courses</option>
            <option value="time_slots">Time Slots</option>
            <option value="semesters">Semesters</option>
            <option value="scheduled_classes">Scheduled Classes</option>
            <option value="students">Students</option>
        </select>

        <!-- Fix Selected Table -->
        <form action="${pageContext.request.contextPath}/admin/IDManagementServlet" method="post"
              id="fixForm" style="display: none;">
            <input type="hidden" name="action" value="fix">
            <input type="hidden" name="tableName" id="fixTableName">
            <button type="submit" class="btn btn-success">
                Fix ID Sequence
            </button>
        </form>

        <!-- Compact Selected Table -->
        <form action="${pageContext.request.contextPath}/admin/IDManagementServlet" method="post"
              id="compactForm" style="display: none;">
            <input type="hidden" name="action" value="compact">
            <input type="hidden" name="tableName" id="compactTableName">
            <button type="submit" class="btn btn-orange">
                Compact IDs
            </button>
        </form>
    </div>
</div>

<script>
// Show/hide table-specific buttons
document.getElementById('tableSelect').addEventListener('change', function() {
    const tableName = this.value;
    const fixForm = document.getElementById('fixForm');
    const compactForm = document.getElementById('compactForm');

    if (tableName) {
        // Set table names in forms
        document.getElementById('fixTableName').value = tableName;
        document.getElementById('compactTableName').value = tableName;

        // Show forms
        fixForm.style.display = 'block';
        compactForm.style.display = 'block';

        // Update button texts
        const fixBtn = fixForm.querySelector('button');
        const compactBtn = compactForm.querySelector('button');
        const tableDisplay = tableName.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase());

        fixBtn.textContent = `Fix ${tableDisplay} IDs`;
        fixBtn.onclick = function() {
            return confirm(`Fix auto-increment for ${tableDisplay} table?`);
        };

        compactBtn.textContent = `Compact ${tableDisplay} IDs`;
        compactBtn.onclick = function() {
            return confirm(`Compact ${tableDisplay} IDs (reorganize sequentially)?`);
        };
    } else {
        fixForm.style.display = 'none';
        compactForm.style.display = 'none';
    }
});
</script>
    <script>
        // Add animation to cards on page load
        document.addEventListener('DOMContentLoaded', function() {
            const cards = document.querySelectorAll('.feature-card');
            cards.forEach((card, index) => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';

                setTimeout(() => {
                    card.style.transition = 'all 0.5s ease';
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, index * 100);
            });
        });
    </script>
</body>
</html>