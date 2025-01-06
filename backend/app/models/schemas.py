from pydantic import BaseModel, IPvAnyAddress
from typing import Optional, List
from datetime import datetime

class UserLogin(BaseModel):
    username: str
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str

class IPRuleBase(BaseModel):
    ip: str
    type: str = "single_ip"
    comment: Optional[str] = None

class IPRuleCreate(IPRuleBase):
    pass

class IPRule(IPRuleBase):
    id: str
    created_at: datetime
    status: str = "active"

class GeoRuleBase(BaseModel):
    country_code: str
    comment: Optional[str] = None

class GeoRuleCreate(GeoRuleBase):
    pass

class GeoRule(GeoRuleBase):
    id: str
    created_at: datetime
    status: str = "active" 