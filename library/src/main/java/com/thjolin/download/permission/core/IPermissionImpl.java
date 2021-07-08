package com.thjolin.download.permission.core;

// 告诉外交
public abstract class IPermissionImpl implements IPermission {

    public void cancel(){} // 取消权限

    public void denied(){} // 拒绝权限

}
