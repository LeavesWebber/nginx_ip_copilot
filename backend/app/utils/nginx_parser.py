import re
from typing import List, Dict

class NginxParser:
    def __init__(self):
        self.ip_block_pattern = r'deny\s+(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(?:/\d{1,2})?);'
        self.geo_block_pattern = r'if\s+\(\$geoip_country_code\s*=\s*([A-Z]{2})\)\s*{\s*return\s+403;\s*}'

    def parse_ip_rules(self, config_content: str) -> List[Dict]:
        """解析IP规则"""
        rules = []
        matches = re.finditer(self.ip_block_pattern, config_content)
        for match in matches:
            rules.append({
                "ip": match.group(1),
                "type": "single_ip" if "/" not in match.group(1) else "ip_range"
            })
        return rules

    def parse_geo_rules(self, config_content: str) -> List[Dict]:
        """解析地理位置规则"""
        rules = []
        matches = re.finditer(self.geo_block_pattern, config_content)
        for match in matches:
            rules.append({
                "country_code": match.group(1)
            })
        return rules

    def generate_ip_rule(self, ip: str, comment: str = None) -> str:
        """生成IP规则配置"""
        rule = f"deny {ip};"
        if comment:
            rule = f"# {comment}\n{rule}"
        return rule

    def generate_geo_rule(self, country_code: str, comment: str = None) -> str:
        """生成地理位置规则配置"""
        rule = f"if ($geoip_country_code = {country_code}) {{ return 403; }}"
        if comment:
            rule = f"# {comment}\n{rule}"
        return rule 