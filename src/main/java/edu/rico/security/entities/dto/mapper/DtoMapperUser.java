package edu.rico.security.entities.dto.mapper;

import edu.rico.security.entities.User;
import edu.rico.security.entities.dto.UserDto;

public class DtoMapperUser {
    
    private User user;

    public DtoMapperUser() {}
    
    public DtoMapperUser(User user){
        this.user = user;
    }

    public static DtoMapperUser builder(){
        return new DtoMapperUser();
    }

    public DtoMapperUser setUser(User user) {
        this.user = user;
        return this;
    }

    public UserDto build(){
        if(user == null){
            throw new RuntimeException("User no puede ser DTO");
        }
        return new UserDto(this.user.getId(),user.getUsername(),user.getEmail());
    }

    

}
