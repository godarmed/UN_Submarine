package com.eseasky.submarine.core.starters.authserver.interfaces;


import com.eseasky.submarine.core.starters.global.entity.UserViews;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserViews getUserByName(String var1);
}
