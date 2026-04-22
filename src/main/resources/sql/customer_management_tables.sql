-- 客户管理系统 - 数据表创建脚本 (PostgreSQL)
-- 创建时间: 2025-04-22

-- 1. 活动表
CREATE TABLE IF NOT EXISTS activities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    location VARCHAR(500),
    max_participants INTEGER,
    registration_start DATE,
    registration_end DATE,
    status VARCHAR(50) DEFAULT '未开始',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 活动表索引
CREATE INDEX IF NOT EXISTS idx_activities_status ON activities(status);
CREATE INDEX IF NOT EXISTS idx_activities_registration_dates ON activities(registration_start, registration_end);

-- 2. 客户表
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100),
    hobby VARCHAR(500),
    region VARCHAR(200),
    emergency_contact VARCHAR(200),
    referrer_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 客户表索引
CREATE INDEX IF NOT EXISTS idx_customers_phone ON customers(phone);
CREATE INDEX IF NOT EXISTS idx_customers_referrer_id ON customers(referrer_id);
CREATE INDEX IF NOT EXISTS idx_customers_region ON customers(region);

-- 3. 报名表
CREATE TABLE IF NOT EXISTS registrations (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    registration_time TIMESTAMP,
    status VARCHAR(50) DEFAULT '已报名',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 报名表索引
CREATE INDEX IF NOT EXISTS idx_registrations_customer_id ON registrations(customer_id);
CREATE INDEX IF NOT EXISTS idx_registrations_activity_id ON registrations(activity_id);
CREATE INDEX IF NOT EXISTS idx_registrations_status ON registrations(status);

-- 4. 跟进记录表
CREATE TABLE IF NOT EXISTS follow_ups (
    id BIGSERIAL PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    follow_up_time TIMESTAMP,
    follower_id BIGINT,
    content TEXT,
    method VARCHAR(50),
    next_follow_up TIMESTAMP,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 跟进记录表索引
CREATE INDEX IF NOT EXISTS idx_follow_ups_registration_id ON follow_ups(registration_id);
CREATE INDEX IF NOT EXISTS idx_follow_ups_follower_id ON follow_ups(follower_id);
CREATE INDEX IF NOT EXISTS idx_follow_ups_follow_up_time ON follow_ups(follow_up_time);
CREATE INDEX IF NOT EXISTS idx_follow_ups_next_follow_up ON follow_ups(next_follow_up);

-- 添加表注释
COMMENT ON TABLE activities IS '活动表';
COMMENT ON TABLE customers IS '客户表';
COMMENT ON TABLE registrations IS '报名表';
COMMENT ON TABLE follow_ups IS '跟进记录表';

-- 添加列注释
COMMENT ON COLUMN activities.id IS '主键ID';
COMMENT ON COLUMN activities.name IS '活动名称';
COMMENT ON COLUMN activities.type IS '活动类型（夏令营/冬令营/其他）';
COMMENT ON COLUMN activities.start_date IS '活动开始日期';
COMMENT ON COLUMN activities.end_date IS '活动结束日期';
COMMENT ON COLUMN activities.location IS '活动地点';
COMMENT ON COLUMN activities.max_participants IS '最大人数';
COMMENT ON COLUMN activities.registration_start IS '报名开始日期';
COMMENT ON COLUMN activities.registration_end IS '报名结束日期';
COMMENT ON COLUMN activities.status IS '状态（未开始/报名中/已结束）';
COMMENT ON COLUMN activities.description IS '活动描述';
COMMENT ON COLUMN activities.created_at IS '创建时间';
COMMENT ON COLUMN activities.updated_at IS '更新时间';

COMMENT ON COLUMN customers.id IS '主键ID';
COMMENT ON COLUMN customers.phone IS '手机号（唯一）';
COMMENT ON COLUMN customers.name IS '姓名';
COMMENT ON COLUMN customers.hobby IS '爱好';
COMMENT ON COLUMN customers.region IS '地区';
COMMENT ON COLUMN customers.emergency_contact IS '紧急联系人';
COMMENT ON COLUMN customers.referrer_id IS '推荐人ID（关联customers表）';
COMMENT ON COLUMN customers.created_at IS '创建时间';
COMMENT ON COLUMN customers.updated_at IS '更新时间';

COMMENT ON COLUMN registrations.id IS '主键ID';
COMMENT ON COLUMN registrations.customer_id IS '客户ID';
COMMENT ON COLUMN registrations.activity_id IS '活动ID';
COMMENT ON COLUMN registrations.registration_time IS '报名时间';
COMMENT ON COLUMN registrations.status IS '状态（已报名/已取消/已完成）';
COMMENT ON COLUMN registrations.remarks IS '备注';
COMMENT ON COLUMN registrations.created_at IS '创建时间';
COMMENT ON COLUMN registrations.updated_at IS '更新时间';

COMMENT ON COLUMN follow_ups.id IS '主键ID';
COMMENT ON COLUMN follow_ups.registration_id IS '报名ID';
COMMENT ON COLUMN follow_ups.follow_up_time IS '跟进时间';
COMMENT ON COLUMN follow_ups.follower_id IS '跟进人ID（关联users表）';
COMMENT ON COLUMN follow_ups.content IS '跟进内容';
COMMENT ON COLUMN follow_ups.method IS '跟进方式（电话/微信/面谈）';
COMMENT ON COLUMN follow_ups.next_follow_up IS '下次跟进时间';
COMMENT ON COLUMN follow_ups.status IS '状态（已联系/跟进中/已报名/已放弃）';
COMMENT ON COLUMN follow_ups.created_at IS '创建时间';
COMMENT ON COLUMN follow_ups.updated_at IS '更新时间';

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为各表创建更新时间触发器
CREATE TRIGGER update_activities_updated_at BEFORE UPDATE ON activities
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_registrations_updated_at BEFORE UPDATE ON registrations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_follow_ups_updated_at BEFORE UPDATE ON follow_ups
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 插入一些示例数据
INSERT INTO activities (name, type, start_date, end_date, location, max_participants, registration_start, registration_end, status, description) VALUES
('2025年暑期夏令营', '夏令营', '2025-07-15', '2025-07-30', '北京怀柔营地', 100, '2025-04-15', '2025-06-30', '报名中', '为期15天的夏令营活动，包含户外拓展、团队建设等内容'),
('2025年英语特训营', '夏令营', '2025-08-01', '2025-08-10', '上海浦东', 50, '2025-05-01', '2025-07-15', '报名中', '英语强化训练，包含口语、听力专项训练'),
('2025年冬令营预热活动', '冬令营', '2025-12-20', '2025-12-30', '成都', 80, '2025-10-01', '2025-12-01', '未开始', '冬季拓展训练活动');
