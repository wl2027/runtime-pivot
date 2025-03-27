#!/bin/bash

# 源仓库路径
SOURCE_REPO="E:\002_Code\000_github\IDEA\runtime-pivot"
# 目标仓库路径
TARGET_REPO="C:\Users\2018wl\Desktop\开源项目迁移和重构2503\runtime-pivot"

# 进入目标仓库目录
cd $TARGET_REPO

# 添加源仓库为远程仓库
git remote add source $SOURCE_REPO

# 从源仓库拉取所有分支
git fetch source

# 在目标仓库中创建并切换到对应分支
for branch in $(git branch -r --list 'source/*' | grep -v HEAD); do
    local_branch_name=$(echo $branch | sed 's/^source\///')
    if ! git show-ref --verify --quiet refs/heads/$local_branch_name; then
        git branch $local_branch_name $branch
    fi
done

# 解决潜在的冲突（手动操作）

# 删除添加的远程仓库（可选）
git remote remove source
