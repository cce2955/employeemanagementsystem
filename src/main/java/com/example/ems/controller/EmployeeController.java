package com.example.ems.controller;



import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ems.Employee;
import com.example.ems.service.EmployeeService;


@Controller
@RequestMapping("/employees")
public class EmployeeController {
	 public boolean isValidEmail(String email) {
	        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
	        Pattern pattern = Pattern.compile(emailRegex);
	        Matcher matcher = pattern.matcher(email);
	        return matcher.matches();
	    }
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String viewEmployeePage(Model model) {
        return findPaginated(1, "firstName", "asc", model);
    }
    
    @GetMapping("/new")
    public String showNewEmployeeForm(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "new_employee";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee, Model model) {
        String email = employee.getEmail();

        if (!isValidEmail(email)) {
            model.addAttribute("emailError", "Invalid email format.");
            return "new_employee";
        }

        if (employeeService.isEmailDuplicate(email)) {
            model.addAttribute("emailError", "Email already exists.");
            return "new_employee";
        }

        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }





    @GetMapping("/edit")
    public String showEditEmployeeForm(@RequestParam("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "edit_employee";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable(value = "id") Long id) {
        employeeService.deleteEmployeeById(id);
        return "redirect:/employees";
    }

    @GetMapping("/page")
    public String findPaginated(@RequestParam("pageNo") int pageNo,
                                 @RequestParam("sortField") String sortField,
                                 @RequestParam("sortDir") String sortDir,
                                 Model model) {
        int pageSize = 5;

        Page<Employee> page = employeeService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Employee> listEmployees = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listEmployees", listEmployees);
        return "index";
    }
    

}
