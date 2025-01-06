#!/bin/bash

# 检查nginx配置语法
nginx -t

if [ $? -eq 0 ]; then
    # 重新加载nginx配置
    nginx -s reload
    echo "Nginx配置已重新加载"
    exit 0
else
    echo "Nginx配置检查失败，请检查配置文件"
    exit 1
fi 