-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    city VARCHAR(50),
    is_employed BOOLEAN DEFAULT true,
    hire_date DATE,
    resignation_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限表（支持四级结构）
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    level INT NOT NULL,
    path VARCHAR(255),
    icon VARCHAR(100),
    sort_order INT DEFAULT 0,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 创建触发器
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_roles_updated_at ON roles;
CREATE TRIGGER update_roles_updated_at
    BEFORE UPDATE ON roles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_permissions_updated_at ON permissions;
CREATE TRIGGER update_permissions_updated_at
    BEFORE UPDATE ON permissions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 插入初始权限数据（四级结构）
-- Level 1: 用户管理
INSERT INTO permissions (parent_id, name, code, type, level, path, icon, sort_order) VALUES
(0, '用户管理', 'user', 'system', 1, '/user', 'user-management', 1);

-- Level 2: 用户中心
INSERT INTO permissions (parent_id, name, code, type, level, path, icon, sort_order) VALUES
(1, '用户中心', 'user:center', 'module', 2, '/user/center', 'user-center', 1);

-- Level 3: 用户列表 和 角色列表
INSERT INTO permissions (parent_id, name, code, type, level, path, icon, sort_order) VALUES
(2, '用户列表', 'user:center:userlist', 'menu', 3, '/user/center/userlist', 'user-list', 1),
(2, '角色列表', 'user:center:rolelist', 'menu', 3, '/user/center/rolelist', 'role-list', 2);

-- Level 4: 用户列表操作
INSERT INTO permissions (parent_id, name, code, type, level, sort_order) VALUES
(3, '创建用户', 'user:userlist:create', 'button', 4, 1),
(3, '编辑用户', 'user:userlist:edit', 'button', 4, 2),
(3, '删除用户', 'user:userlist:delete', 'button', 4, 3);

-- Level 4: 角色列表操作
INSERT INTO permissions (parent_id, name, code, type, level, sort_order) VALUES
(4, '创建角色', 'user:rolelist:create', 'button', 4, 1),
(4, '编辑角色', 'user:rolelist:edit', 'button', 4, 2),
(4, '删除角色', 'user:rolelist:delete', 'button', 4, 3),
(4, '分配权限', 'user:rolelist:assign', 'button', 4, 4);

-- 插入初始角色
INSERT INTO roles (name, code, description) VALUES
('超级管理员', 'admin', '拥有所有权限'),
('普通用户', 'user', '普通用户权限');

-- 为超级管理员分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;
