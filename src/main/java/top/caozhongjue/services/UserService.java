package top.caozhongjue.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caozhongjue.dao.UserMapper;
import top.caozhongjue.pojo.User;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdateUser(User user) {
       User user1 =  userMapper.findByAcountId(user.getAccountId());
        if (user1 == null){
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else{
            user1.setName(user.getName());
            user1.setToken(user.getToken());
            user1.setGmtModified(System.currentTimeMillis());
            user1.setAvatarUrl(user.getAvatarUrl());
            userMapper.update(user1);
        }
    }
}
