from fastapi import APIRouter, Depends, HTTPException
from typing import List
from app.models.schemas import IPRule, IPRuleCreate
from app.services.nginx_service import NginxService
from app.core.security import get_current_user

router = APIRouter()
nginx_service = NginxService()

@router.get("/", response_model=List[IPRule])
async def get_ip_rules():
    """获取所有IP规则"""
    return await nginx_service.get_ip_rules()

@router.post("/", response_model=IPRule)
async def create_ip_rule(rule: IPRuleCreate):
    """创建新的IP规则"""
    return await nginx_service.add_ip_rule(rule)

@router.delete("/{rule_id}")
async def delete_ip_rule(rule_id: str):
    """删除IP规则"""
    success = await nginx_service.delete_ip_rule(rule_id)
    if not success:
        raise HTTPException(status_code=404, detail="规则不存在")
    return {"message": "规则删除成功"} 