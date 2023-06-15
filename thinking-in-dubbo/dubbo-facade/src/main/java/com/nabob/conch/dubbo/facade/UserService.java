package com.nabob.conch.dubbo.facade;

/**
 * User Facade
 *
 * @author Adam
 * @date 2021/4/2
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @return 用户
     */
    User getUserById();

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    String getUserName();

}
