import re
from typing import Optional
from ipaddress import ip_address, ip_network

def validate_ip(ip: str) -> bool:
    """验证IP地址格式"""
    try:
        if '/' in ip:  # CIDR格式
            ip_network(ip)
        else:
            ip_address(ip)
        return True
    except ValueError:
        return False

def validate_country_code(country_code: str) -> bool:
    """验证国家代码格式"""
    return bool(re.match(r'^[A-Z]{2}$', country_code))

def validate_nginx_config_path(path: str) -> bool:
    """验证Nginx配置文件路径"""
    # 简单的路径格式验证
    return bool(re.match(r'^(/[\w.-]+)+$', path))

def sanitize_comment(comment: Optional[str]) -> str:
    """清理和验证注释内容"""
    if not comment:
        return ""
    # 移除可能导致注入的字符
    return re.sub(r'[#;\'"\\]', '', comment) 