from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api import auth, ip_rules, geo_rules
from app.core.config import settings

app = FastAPI(
    title="Nginx IP Copilot",
    description="一个用于管理 Nginx IP 访问控制的工具"
)

# CORS 配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 在生产环境中应该设置具体的源
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册路由
app.include_router(auth.router, prefix="/api/auth", tags=["认证"])
app.include_router(ip_rules.router, prefix="/api/ip-rules", tags=["IP规则"])
app.include_router(geo_rules.router, prefix="/api/geo-rules", tags=["地理位置规则"])

@app.get("/")
async def root():
    return {"message": "欢迎使用 Nginx IP Copilot"} 