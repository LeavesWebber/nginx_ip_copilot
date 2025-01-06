from pydantic_settings import BaseSettings
from typing import Optional

class Settings(BaseSettings):
    # 基础配置
    PROJECT_NAME: str = "Nginx IP Copilot"
    API_V1_STR: str = "/api"
    
    # Nginx 相关配置
    NGINX_CONFIG_PATH: Optional[str] = None  # 用户的nginx配置文件路径
    NGINX_RELOAD_CMD: str = "nginx -s reload"
    
    # 安全配置
    SECRET_KEY: str = "your-secret-key-here"  # 在生产环境中应该更改
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24  # 24 小时
    
    # GeoIP 配置
    GEOIP_API_KEY: Optional[str] = None
    
    # AbuseIPDB 配置
    ABUSEIPDB_API_KEY: Optional[str] = None
    ABUSEIPDB_ENABLED: bool = False

    class Config:
        env_file = ".env"

settings = Settings() 