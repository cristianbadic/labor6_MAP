package com.example.labor6_map;

import com.example.labor6_map.ConsoleView.ConsoleView;
import com.example.labor6_map.Controller.Controller;
import com.example.labor6_map.Exception.CanNotRegister;
import com.example.labor6_map.Exception.DoesNotExistException;
import com.example.labor6_map.Exception.ExistentElementException;
import com.example.labor6_map.Exception.TeacherException;
import com.example.labor6_map.Repository.CourseMySQLRepository;
import com.example.labor6_map.Repository.StudentMySQLRepository;
import com.example.labor6_map.Repository.TeacherMySQLRepository;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] arg) throws SQLException, DoesNotExistException, TeacherException, CanNotRegister, ExistentElementException, IOException {
        TeacherMySQLRepository teachRep = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        StudentMySQLRepository studentRep = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        CourseMySQLRepository courseRep = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");

        Controller contr = new Controller(courseRep, teachRep, studentRep);
        ConsoleView consoleV = new ConsoleView(contr);

        consoleV.run();
    }
}
