from typing import List, Optional
import aiohttp
from app.core.config import settings

class AbuseIPDBService:
    def __init__(self):
        self.api_key = settings.ABUSEIPDB_API_KEY
        self.base_url = "https://api.abuseipdb.com/api/v2"
        self.enabled = settings.ABUSEIPDB_ENABLED

    async def check_ip(self, ip: str) -> dict:
        """检查IP地址的风险等级"""
        if not self.enabled or not self.api_key:
            return {"error": "AbuseIPDB service is not enabled"}

        headers = {
            'Key': self.api_key,
            'Accept': 'application/json',
        }
        
        params = {
            'ipAddress': ip,
            'maxAgeInDays': '90'
        }

        async with aiohttp.ClientSession() as session:
            async with session.get(
                f"{self.base_url}/check",
                headers=headers,
                params=params
            ) as response:
                return await response.json()

    async def get_blacklist(self, confidence: int = 90) -> List[str]:
        """获取高风险IP黑名单"""
        if not self.enabled or not self.api_key:
            return []

        headers = {
            'Key': self.api_key,
            'Accept': 'application/json',
        }
        
        params = {
            'confidenceMinimum': confidence
        }

        async with aiohttp.ClientSession() as session:
            async with session.get(
                f"{self.base_url}/blacklist",
                headers=headers,
                params=params
            ) as response:
                data = await response.json()
                return data.get("data", []) 