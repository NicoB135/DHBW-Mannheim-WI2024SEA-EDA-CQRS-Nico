from dataclasses import dataclass
from datetime import datetime
from ..core.base_command import BaseCommand


@dataclass
class CreateAuction(BaseCommand):
    auction_id: str
    title: str
    starting_price: float
    ends_at: datetime
