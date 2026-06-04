#!/bin/bash

# 원격 저장소 디렉터리 생성
mkdir -p /app/data/origin

# 베어 저장소 초기화
git init --bare /app/data/origin/default.git

echo "✓ Bare repository initialized at /app/data/origin/default.git"
