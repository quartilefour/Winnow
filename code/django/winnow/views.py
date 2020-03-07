from django.shortcuts import render
from django.contrib.auth import authenticate, login, logout
from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.urls import reverse
from django.contrib.auth.models import User
from django.db.utils import IntegrityError

from .models import Address, Author, Gene, GeneGene, GeneGoterm, GeneMeshterm, GenePublication, \
    GeneRelationship, Goterm, Meshterm, Publication, PublicationAuthor, PublicationMeshterm, Search, \
    Team, UserSearchSharing, UserTeam, UserExtension

# Create your views here.

# Default root to the site, if the user is not logged in the system.
def index(request):
    if not request.user.is_authenticated:
        return render(request, "winnow/login.html", {"message": None})
    else:
        return render(request, "winnow/invoices.html", {"message": "Welcome to gfn"})


# view to be rendered post login with a verified username/password, it sets up the account type to be used for the rest of the session
def login_view(request):
    username = request.POST["username"]
    password = request.POST["password"]
    if 'accounttype' not in request.POST:
        return render(request, "winnow/login.html",
                      {"reason": 'Please select the Account type to login into the system'})

    accounttype = request.POST["accounttype"]

    user = authenticate(request, username=username, password=password)
    if user is not None:
        if accounttype == 'Supplier' and not user.extendeduser.supplier:
            return render(request, "winnow/login.html",
                          {"reason": 'User doesnot have supplier account, please contact admin for upgrade'})
        elif accounttype == 'Customer' and not user.extendeduser.customer:
            return render(request, "winnow/login.html",
                          {"reason": 'User doesnot have customer account, please contact admin for upgrade'})

        login(request, user)
        request.session['accounttype'] = accounttype
        return HttpResponseRedirect(reverse("index"))
    else:
        return render(request, "winnow/login.html", {"message": "Invalid credentials."})


# view to be rendered for new user registration
def new_user_register_view(request):
    return render(request, "winnow/register.html")



# view to register a user based on post parameters passed in the request.
def register_view(request):
    username = request.POST["username"]
    email = request.POST["email"]
    firstname = request.POST["firstname"]
    lastname = request.POST["lastname"]
    password = request.POST["password"]

    try:
        user = User.objects.create_user(username, email=email, password=password, first_name=firstname,
                                        last_name=lastname)

        user_extension = UserExtension.objects.create(user_id=user, user_email=email, first_name=firstname, last_name=lastname)

        Address.objects.create(address1=request.POST["address-line1"], address2=request.POST["address-line2"],
                               city=request.POST["city"], state=request.POST["region"],
                               country=request.POST["country"], user=user_extension, zipcode=request.POST["postal-code"])


        return render(request, "winnow/login.html", {"message": f"Account successfaully created for {username}"})

    except IntegrityError as e:
        return render(request, "winnow/register.html",
                      {"error_message": f"Username {username} already has an account. Please login"})


# view to be rendered post logout
def logout_view(request):
    logout(request)
    return render(request, "winnow/login.html", {"message": "Logged out."})

