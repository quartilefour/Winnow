from django.contrib import admin

# Register your models here.
from .models import Address, Author, Gene, GeneGene, GeneGoterm, GeneMeshterm, GenePublication, \
    GeneRelationship, Goterm, Meshterm, Publication, PublicationAuthor, PublicationMeshterm, Search, \
    Team, UserSearchSharing, UserTeam, UserExtension

admin.site.register(Address)
admin.site.register(Author)
admin.site.register(Gene)
admin.site.register(GeneGene)
admin.site.register(GeneGoterm)
admin.site.register(GeneMeshterm)
admin.site.register(GenePublication)
admin.site.register(GeneRelationship)
admin.site.register(Goterm)
admin.site.register(Meshterm)
admin.site.register(Publication)
admin.site.register(PublicationAuthor)
admin.site.register(PublicationMeshterm)
admin.site.register(Search)
admin.site.register(Team)
admin.site.register(UserSearchSharing)
admin.site.register(UserTeam)
admin.site.register(UserExtension)

