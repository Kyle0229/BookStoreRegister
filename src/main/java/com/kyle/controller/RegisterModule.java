package com.kyle.controller;

import com.kyle.domain.Author;
import com.kyle.domain.User;
import com.kyle.request.UserCode;
import com.kyle.service.AuthorService;
import com.kyle.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class RegisterModule {

    @Resource
    private UserService userService;
    @Resource
    private AuthorService authorService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    /*
     * 注册
     */
    @RequestMapping("/register")
    @ResponseBody
    public String register(@RequestBody UserCode userCode){
        String msg="";
        User user=userCode.getUser();
        //String code=userCode.getCode();
        String email=user.getUemail();
        String name=user.getUname();
        if (userService.findByUname(name)!=null){
            msg="用户名已存在";
            return msg;
       }
        user.setUstatus(0);
        User save = userService.save(user);
       if(save!=null) {
            rabbitTemplate.convertAndSend("exchange", "topic.message", email+","+save.getUid());
            System.out.println("######################邮件已发送#########################");
       }

            return "success";
    }

    /*
     * 注册
     */
    @RequestMapping("/authorRegister")
    @ResponseBody
    public String register(@RequestBody Author shoper){
        String msg="";
        String name=shoper.getAname();
        if (authorService.findByAname(name)!=null){
            msg="用户名已存在";
            return msg;
        } else {
            authorService.save(shoper);
            return "success";
        }
    }
    /*
    * 邮件验证
    * */
    @RequestMapping("/updateStatus")
    public String updateStatus(@RequestParam Integer uid){
        User user=userService.selectOne(uid);
        if(user.getUstatus()==0) {
            user.setUstatus(1);
            userService.saveU(user);
            return "success.html";
        }
        return "fail.html";
    }
}
