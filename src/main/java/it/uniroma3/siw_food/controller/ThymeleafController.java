package it.uniroma3.siw_food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Employee;
import it.uniroma3.siw_food.repository.EmployeeRepository;

@Controller
public class ThymeleafController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "list-employees";
    }

    @GetMapping("/employees/new")
    public String createEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "create-employee";
    }

    @PostMapping("/employees")
    public String saveEmployee(Employee employee) {
        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmailId(employeeDetails.getEmailId());

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployeeForm(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
        model.addAttribute("employee", employee);
        return "delete-employee";
    }

    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
        employeeRepository.delete(employee);
        return "redirect:/employees";
    }
}
