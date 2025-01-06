from fastapi import APIRouter, Depends, HTTPException
from typing import List
from app.models.schemas import GeoRule, GeoRuleCreate
from app.services.geoip_service import GeoIPService
from app.core.security import get_current_user

router = APIRouter()
geoip_service = GeoIPService()

@router.get("/", response_model=List[GeoRule])
async def get_geo_rules():
    """获取所有地理位置规则"""
    return await geoip_service.get_geo_rules()

@router.post("/", response_model=GeoRule)
async def create_geo_rule(rule: GeoRuleCreate):
    """创建新的地理位置规则"""
    return await geoip_service.add_geo_rule(rule)

@router.delete("/{rule_id}")
async def delete_geo_rule(rule_id: str):
    """删除地理位置规则"""
    success = await geoip_service.delete_geo_rule(rule_id)
    if not success:
        raise HTTPException(status_code=404, detail="规则不存在")
    return {"message": "规则删除成功"} 