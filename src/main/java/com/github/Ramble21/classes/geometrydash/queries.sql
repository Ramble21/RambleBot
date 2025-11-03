-- Documentation file of all SQL queries located in GDDatabase.java
-- This is mostly just in the program to help with code formatting and syntax issues

-- getLevel
SELECT *
FROM levels
WHERE id = ?;

-- getLevelFromNameAuthor
SELECT *
FROM levels
WHERE name ILIKE ?
    AND author ILIKE ?

-- getLevelFromNameDiff
SELECT *
FROM levels
WHERE name ILIKE ?
    AND difficulty ILIKE ?

-- getRecord
SELECT r.*, l.*
FROM records r
JOIN levels l ON r.level_id = l.id
WHERE r.level_id = ?
    AND r.submitter_id = ?;

-- getGuildLevels
SELECT DISTINCT l.*
FROM levels l
JOIN records r ON l.id = r.level_id
JOIN guild_members gm ON r.submitter_id = gm.user_id
WHERE gm.guild_id = ?
    AND l.platformer = ?
    AND r.record_accepted = TRUE;

-- getMemberRecords
SELECT r.*, l.*
FROM records r
JOIN levels l ON r.level_id = l.id
WHERE r.submitter_id = ?
    AND l.platformer = ?
    AND r.record_accepted = TRUE;

-- addRecord
    -- check
    SELECT 1
    FROM records
    WHERE submitter_id = ?
        AND level_id = ?;
    -- insert level
    INSERT INTO levels (id, author, difficulty, gddl_tier, name, platformer, rating, stars)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (id) DO NOTHING;
    -- insert record
    INSERT INTO records (submitter_id, attempts, bias_level, record_accepted, level_id)
        VALUES (?, ?, ?, ?, ?);

-- getMemberStatus
SELECT member_status
FROM members
WHERE user_id = ?;

-- changeMemberStatus
UPDATE members
SET member_status = ?
WHERE user_id = ?

-- addMemberToDatabase
    -- members
    INSERT INTO members (user_id, username)
        VALUES (?, ?)
        ON CONFLICT DO NOTHING;
    -- guilds
    INSERT INTO guilds (guild_id, name)
        VALUES (?, ?)
        ON CONFLICT (guild_id)
        DO UPDATE SET name = EXCLUDED.name;
    -- guild_members
    INSERT INTO guild_members (guild_id, user_id)
        VALUES (?, ?)
        ON CONFLICT DO NOTHING;

-- deleteRecord
DELETE FROM records
WHERE submitter_id = ?
    AND level_id = ?;

-- acceptRecord
UPDATE records
SET record_accepted = TRUE
WHERE submitter_id = ?
    AND level_id = ?;

-- editRecord
UPDATE records
SET bias_level = ?, attempts = ?
WHERE submitter_id = ?
    AND level_id = ?;

-- updateAllLevels
    -- select
    SELECT id, difficulty, gddl_tier
        FROM levels;
    -- update
    UPDATE levels
    SET difficulty = ?, gddl_tier = ?
    WHERE id = ?;

-- updateNullLevels
    -- select
    SELECT id, difficulty, gddl_tier
    FROM levels
    WHERE difficulty IS NULL
        OR gddl_tier = 0;
    -- update
    UPDATE levels
    SET difficulty = ?, gddl_tier = ?
    WHERE id = ?;

-- getUnverifiedRecords
SELECT r.*, l.*, m.username
FROM records r
JOIN levels l ON r.level_id = l.id
JOIN members m ON r.submitter_id = m.user_id
JOIN guild_members gm ON r.submitter_id = gm.user_id
WHERE gm.guild_id = ?
    AND r.record_accepted = FALSE;

-- recreate database
BEGIN;

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE levels (
    id BIGINT PRIMARY KEY,
    name TEXT NOT NULL,
    stars INT,
    author TEXT NOT NULL,
    difficulty TEXT,
    gddl_tier DECIMAL,
    platformer BOOLEAN DEFAULT FALSE,
    rating TEXT,
    UNIQUE (name, author)
);

CREATE TABLE members (
    user_id BIGINT PRIMARY KEY,
    username TEXT NOT NULL,
    member_status STRING DEFAULT ""
);

CREATE TABLE guilds (
    guild_id BIGINT PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE records (
    record_id BIGSERIAL PRIMARY KEY,
    submitter_id BIGINT NOT NULL REFERENCES members(user_id) ON DELETE CASCADE,
    attempts INT DEFAULT 0,
    bias_level INT DEFAULT 0,
    record_accepted BOOLEAN DEFAULT FALSE,
    level_id BIGINT NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
    UNIQUE (submitter_id, level_id)
);
CREATE INDEX records_submitter_index ON records(submitter_id);
CREATE INDEX records_level_index ON records(level_id);

CREATE TABLE guild_members (
    guild_id BIGINT NOT NULL REFERENCES guilds(guild_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES members(user_id) ON DELETE CASCADE,
    PRIMARY KEY (guild_id, user_id)
);
CREATE INDEX gm_user_index ON guild_members(user_id);
CREATE INDEX gm_guild_index ON guild_members(guild_id);

CREATE INDEX levels_platformer_index ON levels(platformer);
CREATE INDEX records_accepted_index ON records(record_accepted);

CREATE INDEX levels_difficulty_index ON levels(difficulty);
CREATE INDEX levels_gddl_tier_index ON levels(gddl_tier);

CREATE INDEX levels_name_lower_index ON levels(LOWER(name));
CREATE INDEX levels_author_lower_index ON levels(LOWER(author));

CREATE INDEX records_guild_verification_index ON records(record_accepted, level_id) WHERE record_accepted = FALSE;

COMMIT;
