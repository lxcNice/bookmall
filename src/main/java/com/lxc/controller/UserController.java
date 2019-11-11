package com.lxc.controller;

import com.lxc.entity.User;
import com.lxc.constants.AddResults;
import com.lxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 用户的表现层
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/403")
    public String unauthorized() {

        return "403";
    }
    /**
     * 用户后台管理页面
     * @param model
     * @return
     */
    @GetMapping("users")
    public String findAll(Model model) {

        model.addAttribute("users", userService.findAll());
        return "userManagement/users.html";
    }

    /**
     * 用户添加的跳转
     * @return
     */
    @GetMapping("add")
    public String toAddUser() {
        return "userManagement/addUser.html";
    }

    /**
     * 用户添加
     * @param user
     * @return
     */
    @PostMapping("add")
    public String add(User user, Model model) {

        if (AddResults.FAIL.equals(userService.addUser(user))) {
            model.addAttribute("addUser", "新增用户失败，该用户名已存在！");
            return "userManagement/addUser.html";
        }
        return "redirect:/user/users";
    }

    @GetMapping("updateProfile/{userId}")
    public String toUpdateProfile(@PathVariable("userId") Integer id, Model model) {

        model.addAttribute("user", userService.findById(id));
        return "user/profile.html";
    }

    @PutMapping("updateProfile")
    public String updateProfile(User user) {

        userService.update(user);
        return "index";
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequestMapping("delete/{userId}")
    public String delete(@PathVariable("userId") Integer id) {

        userService.deleteById(id);
        return "redirect:/user/users";
    }

    /**
     * 升为管理员
     * @param id
     * @return
     */
    @RequestMapping("changeRoleToAdmin/{userId}")
    public String changeRoleToAdmin(@PathVariable("userId") Integer id) {

        userService.changeRoleToAdmin(id);
        return "redirect:/user/users";
    }

    /**
     * 降为普通用户
     * @param id
     * @return
     */
    @RequestMapping("changeRoleToCustomer/{userId}")
    public String changeRoleToCustomer(@PathVariable("userId") Integer id) {

        userService.changeRoleToCustomer(id);
        return "redirect:/user/users";
    }
}
