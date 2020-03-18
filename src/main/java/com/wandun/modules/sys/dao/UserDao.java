package com.wandun.modules.sys.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface UserDao extends CrudDao<User, String> {
    /**
     * 根据登录名称查询用户
     *
     * @param user
     * @return
     */
    User getByLoginName(User user);

    /**
     * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
     *
     * @param user
     * @return
     */
    List<User> findUserByOfficeId(User user);

    /**
     * 查询全部用户数目
     *
     * @return
     */
    long findAllCount(User user);

    /**
     * 更新用户密码
     *
     * @param user
     * @return
     */
    int updatePasswordById(User user);

    /**
     * 更新登录信息，如：登录IP、登录时间
     *
     * @param user
     * @return
     */
    int updateLoginInfo(User user);

    /**
     * 删除用户角色关联数据
     *
     * @param user
     * @return
     */
    int deleteUserRole(User user);

    /**
     * 插入用户角色关联数据
     *
     * @param user
     * @return
     */
    int insertUserRole(User user);

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    int updateUserInfo(User user);

    /**
     * 插入好友
     */
    int insertFriend(@Param("id") String id, @Param("userId") String userId, @Param("friendId") String friendId);

    /**
     * 查找好友
     */
    User findFriend(@Param("userId") String userId, @Param("friendId") String friendId);

    /**
     * 删除好友
     */
    void deleteFriend(@Param("userId") String userId, @Param("friendId") String friendId);

    /**
     * 获取我的好友列表
     */
    List<User> findFriends(User currentUser);

    /**
     * 查询用户-->用来添加到常用联系人
     */
    List<User> searchUsers(User user);

    /**
     *
     */

    List<User> findListByOffice(User user);
}  