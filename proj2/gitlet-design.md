# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Commit

#### Fields

1. String message - 提交信息
2. String parent - 父提交 SHA-1
3. String secondParent - 第二父提交 SHA-1（合并时使用）
4. Date timestamp - 提交时间戳
5. Map<String, String> files - 文件名到 blob SHA-1 的映射
6. String id - 提交的 SHA-1 ID

### Repository

#### Fields

1. File CWD - 当前工作目录
2. File GITLET_DIR - .gitlet 目录
3. File OBJECTS_DIR - objects 目录
4. File REFS_DIR - refs 目录
5. File HEADS_DIR - refs/heads 目录
6. File HEAD_FILE - HEAD 文件

## Algorithms

### init

1. 检查 .gitlet 是否已存在
2. 创建目录结构
3. 创建初始提交（时间戳为 epoch）
4. 创建 master 分支指向初始提交
5. HEAD 指向 master

### add

1. 读取文件内容
2. 计算 blob SHA-1
3. 将文件添加到 addstage
4. 如果文件在 removestage 中，移除

### commit

1. 检查暂存区是否为空
2. 从当前提交复制文件映射
3. 应用暂存区的修改
4. 创建新提交对象
5. 更新当前分支指针
6. 清空暂存区

## Persistence

### 目录结构

```
.gitlet/
  ├── objects/          # 存储 commit 和 blob
  ├── refs/
  │   └── heads/        # 分支指针
  │       └── master    # 文件内容：commit SHA-1
  ├── HEAD              # 当前分支名（如 "master"）
  ├── addstage          # 暂存区
  └── removestage       # 移除暂存区
```

### 存储方式

- Commit: 序列化存储在 objects/[SHA-1]
- Blob: 文件内容存储在 objects/[SHA-1]
- Branch: 文本文件存储在 refs/heads/[分支名]，内容为 commit SHA-1
- HEAD: 文本文件，内容为当前分支名
- Stage: 序列化的 Map<String, String>
