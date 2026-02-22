from dataclasses import dataclass
from ..core.base_command import BaseCommand


@dataclass
class EndAuction(BaseCommand):
    auction_id: str
