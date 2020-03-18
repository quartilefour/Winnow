package com.cscie599.gfn.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
@NamedQueries({
        @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r")
})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "role_name", length = 40)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String id) {
        this.roleId = id;
    }

    public String getName() {
        return roleName;
    }

    public void setName(String name) {
        this.roleName = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}