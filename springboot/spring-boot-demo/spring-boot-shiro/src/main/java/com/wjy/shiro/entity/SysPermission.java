package com.wjy.shiro.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SysPermission implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;//主键.
    private String name;//名称.
    @Column(columnDefinition = "enum('menu','button')")
    private String resourceType;//资源类型，[menu|button]
    private String url;//资源路径.
    private String permission; //权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view
    private Long parentId; //父编号
    private String parentIds; //父编号列表
    private Boolean available = Boolean.FALSE;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("permissions")
    @JoinTable(name = "SysRolePermission", joinColumns = {@JoinColumn(name = "permissionId")}, inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private List<SysRole> roles;

    // 省略 get set 方法

    public String toString() {
        return "SysPermission(id=" + this.getId() + ", name=" + this.getName() + ", " +
                "resourceType=" + this.getResourceType() + ", url=" + this.getUrl() + ", " +
                "permission=" + this.getPermission() + ", parentId=" + this.getParentId() + ", " +
                "parentIds=" + this.getParentIds() + ", available=" + this.getAvailable() + ")";
    }
}