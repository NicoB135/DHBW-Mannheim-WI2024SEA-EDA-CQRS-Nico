from dataclasses import dataclass
from ..core.base_command import BaseCommand


@dataclass
class PlaceBid(BaseCommand):
    auction_id: str
    bidder_name: str
    amount: float
