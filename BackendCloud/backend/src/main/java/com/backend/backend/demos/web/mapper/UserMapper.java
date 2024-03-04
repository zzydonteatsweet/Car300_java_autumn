package com.backend.backend.demos.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.backend.demos.web.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
