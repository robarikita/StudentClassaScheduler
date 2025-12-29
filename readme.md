

A.Frist

cd C:\Users\HP\Desktop\StudentClassScheduler
mvn clean package

B. SECOND

copy C:\Users\HP\Desktop\StudentClassScheduler\target\StudentClassScheduler.war C:\apache-tomcat-9.0.113\webapps\
cd C:\apache-tomcat-9.0.113\bin
.\startup.bat

////////////////////////
TEST YOUR APPLICATION NOW:
Start Here → Home Page:
🔗 http://localhost:8080/StudentClassScheduler/

Should show a welcome page with login buttons

Login Page:
🔗 http://localhost:8080/StudentClassScheduler/login.html

Should display the login form

Try these credentials:

Admin: admin / admin123

Student: student1 / student123

After Login (if successful):
Admin login → Redirects to: http://localhost:8080/StudentClassScheduler/admin/dashboard.jsp

Direct Admin Pages (require login):
🔗 http://localhost:8080/StudentClassScheduler/admin/dashboard.html

Must be logged in as admin

🔗 http://localhost:8080/StudentClassScheduler/admin/add-course.html

Add course form

🔗 http://localhost:8080/StudentClassScheduler/admin/ListCoursesServlet

Should show courses table (after login)

🔗 http://localhost:8080/StudentClassScheduler/admin/courses.jsp

Fixed version (after login)

🧪 Quick Test Sequence (Copy & Paste):
http://localhost:8080/StudentClassScheduler/

http://localhost:8080/StudentClassScheduler/login.html

Login with: admin / admin123

Should go to: http://localhost:8080/StudentClassScheduler/admin/dashboard.html

Click "View All Courses"

Should go to: http://localhost:8080/StudentClassScheduler/admin/ListCoursesServlet
