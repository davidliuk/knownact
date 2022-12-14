package cn.neud.knownact.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.neud.knownact.common.annotation.LogOperation;
import cn.neud.knownact.common.utils.Result;
import cn.neud.knownact.common.exception.ErrorCode;
import cn.neud.knownact.model.dto.user.UserDTO;
import cn.neud.knownact.model.dto.user.*;
import cn.neud.knownact.model.entity.user.UserEntity;
import cn.neud.knownact.model.vo.UserVO;
import cn.neud.knownact.user.service.FollowService;
import cn.neud.knownact.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import cn.neud.knownact.model.dto.page.DeleteRequest;
import cn.neud.knownact.common.utils.ResultUtils;
import cn.neud.knownact.common.exception.BusinessException;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author david
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FollowService followService;

    // region 登录相关

    @GetMapping("/session")
    public String session(HttpServletRequest request) {
        return "session: " + request.getSession().getId() + "  port: " + request.getServerPort();
    }

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<UserEntity> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @SaCheckLogin
    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @SaCheckLogin
    @GetMapping("/get/login")
    public Result<UserDTO> getLoginUser(HttpServletRequest request) {
        UserEntity user = userService.getLoginUser(request);
        UserDTO userVO = new UserDTO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.insert(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.deleteById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public Result<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    @ApiOperation("根据ID获取获取用户视图")
    @LogOperation("根据ID获取获取用户视图")
    public Result<UserVO> getUserById(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity user = userService.selectById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        if (StpUtil.isLogin()) {
            userVO.setHasFollow(followService.isFollow(id));
        }
        return ResultUtils.success(userVO);
    }

    /**
     * 根据 id 批量获取用户
     *
     * @param ids
     * @return
     */
    @PostMapping("/get")
    @ApiOperation("根据ID获取获取用户视图")
    @LogOperation("根据ID获取获取用户视图")
    public Result<Map<Long, UserVO>> getUserByIdBatch(@RequestBody Long[] ids) {
        Map<Long, UserVO> map = new HashMap<>();
        System.out.println("ids!!!!!!!!!");
        System.out.println(ids);
        for (Long id: ids) {
            if (id <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            UserEntity user = userService.selectById(id);
            if (user == null) {
                continue;
            }
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            if (StpUtil.isLogin()) {
                userVO.setHasFollow(followService.isFollow(id));
            }
            map.put(id, userVO);
        }
        return ResultUtils.success(map);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public Result<List<UserDTO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        UserEntity userQuery = new UserEntity();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>(userQuery);
        List<UserEntity> userList = userService.list(queryWrapper);
        List<UserDTO> userVOList = userList.stream().map(user -> {
            UserDTO userVO = new UserDTO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public Result<Page<UserDTO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        UserEntity userQuery = new UserEntity();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>(userQuery);
        Page<UserEntity> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserDTO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserDTO> userVOList = userPage.getRecords().stream().map(user -> {
            UserDTO userVO = new UserDTO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    // endregion
}
