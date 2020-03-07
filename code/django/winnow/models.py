from django.db import models
from django.contrib.auth.models import User


# Create your models here.

class Author(models.Model):
    author_id = models.CharField(primary_key=True, max_length=50)
    fore_name = models.CharField(max_length=30, blank=True, null=True)
    last_name = models.CharField(max_length=30, blank=True, null=True)

    class Meta:
        db_table = 'author'


class Gene(models.Model):
    gene_id = models.CharField(primary_key=True, max_length=20)
    symbol = models.CharField(max_length=40, blank=True, null=True)
    type = models.CharField(max_length=20, blank=True, null=True)
    description = models.CharField(max_length=100, blank=True, null=True)
    publication_count = models.IntegerField(blank=True, null=True)
    synonym = models.CharField(max_length=20, blank=True, null=True)
    modification_date = models.DateField(blank=True, null=True)
    count_modification_time = models.DateTimeField(blank=True, null=True)

    class Meta:
        db_table = 'gene'


class GeneGene(models.Model):
    gene = models.OneToOneField(Gene, models.DO_NOTHING, primary_key=True, related_name='gene_id1')
    other_gene_id = models.ForeignKey(Gene, models.DO_NOTHING, db_column='other_gene_id', related_name='gene_id2')  # Field name made lowercase.
    relationship = models.ForeignKey('GeneRelationship', models.DO_NOTHING)

    class Meta:
        db_table = 'gene_gene'
        unique_together = (('gene', 'other_gene_id', 'relationship'),)


class GeneGoterm(models.Model):
    gene = models.OneToOneField(Gene, models.DO_NOTHING, primary_key=True)
    go = models.ForeignKey('Goterm', models.DO_NOTHING)

    class Meta:
        db_table = 'gene_goterm'
        unique_together = (('gene', 'go'),)


class GeneMeshterm(models.Model):
    gene = models.OneToOneField(Gene, models.DO_NOTHING, primary_key=True)
    mesh = models.ForeignKey('Meshterm', models.DO_NOTHING)
    p_value = models.FloatField(db_column='p-value', blank=True, null=True)  # Field renamed to remove unsuitable characters.
    publication_count = models.IntegerField(blank=True, null=True)

    class Meta:
        db_table = 'gene_meshterm'
        unique_together = (('gene', 'mesh'),)


class GenePublication(models.Model):
    gene = models.OneToOneField(Gene, models.DO_NOTHING, primary_key=True)
    publication = models.ForeignKey('Publication', models.DO_NOTHING)

    class Meta:
        db_table = 'gene_publication'
        unique_together = (('gene', 'publication'),)


class GeneRelationship(models.Model):
    relationship_id = models.CharField(primary_key=True, max_length=20)
    name = models.CharField(max_length=50, blank=True, null=True)

    class Meta:
        db_table = 'gene_relationship'


class Goterm(models.Model):
    go_id = models.CharField(primary_key=True, max_length=20)
    definition = models.CharField(max_length=50, blank=True, null=True)
    xrefs = models.CharField(max_length=30, blank=True, null=True)
    type = models.CharField(max_length=30, blank=True, null=True)

    class Meta:
        db_table = 'goterm'


class Meshterm(models.Model):
    mesh_id = models.CharField(primary_key=True, max_length=20)
    parent_descriptor_id = models.CharField(max_length=20, blank=True, null=True)
    publication_count = models.IntegerField(blank=True, null=True)
    date_created = models.DateField(blank=True, null=True)
    date_revised = models.DateField(blank=True, null=True)
    note = models.CharField(max_length=100, blank=True, null=True)
    supplemental_id = models.CharField(max_length=20, blank=True, null=True)
    name = models.CharField(max_length=30, blank=True, null=True)

    class Meta:
        db_table = 'meshterm'


class Publication(models.Model):
    publication_id = models.CharField(primary_key=True, max_length=20)
    completed_date = models.DateField(blank=True, null=True)
    date_revised = models.DateField(blank=True, null=True)

    class Meta:
        db_table = 'publication'


class PublicationAuthor(models.Model):
    publication = models.OneToOneField(Publication, models.DO_NOTHING, primary_key=True)
    author = models.ForeignKey(Author, models.DO_NOTHING)

    class Meta:
        db_table = 'publication_author'
        unique_together = (('publication', 'author'),)


class PublicationMeshterm(models.Model):
    publication = models.OneToOneField(Publication, models.DO_NOTHING, primary_key=True)
    mesh = models.ForeignKey(Meshterm, models.DO_NOTHING)

    class Meta:
        db_table = 'publication_meshterm'
        unique_together = (('publication', 'mesh'),)


class Search(models.Model):
    search_id = models.CharField(primary_key=True, max_length=20)
    created_by = models.ForeignKey('UserExtension', models.DO_NOTHING, db_column='created_by', blank=True, null=True)
    created_date = models.DateTimeField(blank=True, null=True)
    search_name = models.CharField(max_length=20, blank=True, null=True)
    search_query = models.TextField(blank=True, null=True)  # This field type is a guess.
    deleted_date = models.DateTimeField(blank=True, null=True)
    updated_at = models.DateTimeField(blank=True, null=True)
    team = models.ForeignKey('Team', models.DO_NOTHING, blank=True, null=True)
    query_type = models.CharField(max_length=20, blank=True, null=True)

    class Meta:
        db_table = 'search'


class Team(models.Model):
    team_id = models.CharField(primary_key=True, max_length=20)
    team_lead_id = models.CharField(max_length=20, blank=True, null=True)
    description = models.CharField(max_length=100, blank=True, null=True)

    class Meta:
        db_table = 'team'

# Referenced https://simpleisbetterthancomplex.com/tutorial/2016/07/22/how-to-extend-django-user-model.html to look at how to extend existing DJango models
class UserExtension(models.Model):
    user_id = models.OneToOneField(User, on_delete=models.CASCADE, db_column='user_id', primary_key=True)
    user_email = models.CharField(max_length=100, blank=True, null=True)
    first_name = models.CharField(max_length=40, blank=True, null=True)
    last_name = models.CharField(max_length=40, blank=True, null=True)

    def __str__(self):
        return f"{self.user.id} - {self.supplier} - {self.customer}  - {self.businessName}"

    class Meta:
        db_table = 'user_extension'

class Address(models.Model):
    user = models.OneToOneField('UserExtension', models.DO_NOTHING, primary_key=True)
    address1 = models.CharField(max_length=100, blank=True, null=True)
    address2 = models.CharField(max_length=100, blank=True, null=True)
    city = models.CharField(max_length=20, blank=True, null=True)
    zipcode = models.CharField(max_length=10, blank=True, null=True)
    state = models.CharField(max_length=20, blank=True, null=True)
    country = models.CharField(max_length=20, blank=True, null=True)

    class Meta:
        db_table = 'address'

class UserSearchSharing(models.Model):
    search = models.OneToOneField(Search, models.DO_NOTHING, primary_key=True)
    user = models.ForeignKey(UserExtension, models.DO_NOTHING, related_name='search_shared_to_user')
    shared_by = models.ForeignKey(UserExtension, models.DO_NOTHING, db_column='shared_by', blank=True, null=True, related_name='search_shared_by_user')
    shared_date = models.DateTimeField(blank=True, null=True)
    deleted_date = models.DateTimeField(blank=True, null=True)

    class Meta:
        db_table = 'user_search_sharing'
        unique_together = (('search', 'user'),)


class UserTeam(models.Model):
    team = models.OneToOneField(Team, models.DO_NOTHING, primary_key=True)
    user = models.ForeignKey(UserExtension, models.DO_NOTHING)
    created_date = models.DateTimeField(blank=True, null=True)
    deleted_date = models.DateTimeField(blank=True, null=True)

    class Meta:
        db_table = 'user_team'
        unique_together = (('team', 'user'),)


