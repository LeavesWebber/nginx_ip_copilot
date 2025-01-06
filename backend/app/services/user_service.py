from typing import Optional
import json
import os
from app.core.security import verify_password, get_password_hash

class UserService:
    def __init__(self):
        self.users_file = "users.json"
        self._load_users()

    def _load_users(self):
        """从JSON文件加载用户数据"""
        if os.path.exists(self.users_file):
            with open(self.users_file, 'r') as f:
                self.users = json.load(f)
        else:
            # 创建默认管理员账户
            self.users = {
                "admin": {
                    "username": "admin",
                    "hashed_password": get_password_hash("admin123")
                }
            }
            self._save_users()

    def _save_users(self):
        """保存用户数据到JSON文件"""
        with open(self.users_file, 'w') as f:
            json.dump(self.users, f, indent=2)

    async def authenticate_user(self, username: str, password: str) -> Optional[dict]:
        """验证用户"""
        if username not in self.users:
            return None
        user = self.users[username]
        if not verify_password(password, user["hashed_password"]):
            return None
        return user 