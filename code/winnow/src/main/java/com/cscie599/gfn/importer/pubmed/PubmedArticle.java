package com.cscie599.gfn.importer.pubmed;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@XmlRootElement(name = "PubmedArticle")
public class PubmedArticle {
    MedlineCitation MedlineCitation;
    PubmedData PubmedData;


    // Getter Methods

    public MedlineCitation getMedlineCitation() {
        return MedlineCitation;
    }

    public PubmedData getPubmedData() {
        return PubmedData;
    }

    // Setter Methods

    public void setMedlineCitation(MedlineCitation MedlineCitationObject) {
        this.MedlineCitation = MedlineCitationObject;
    }

    public void setPubmedData(PubmedData PubmedDataObject) {
        this.PubmedData = PubmedDataObject;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @XmlRootElement(name = "PubmedData")
    public static class PubmedData {
        private String PublicationStatus;
        ArticleIdList ArticleIdListObject;


        public String getPublicationStatus() {
            return PublicationStatus;
        }

        public ArticleIdList getArticleIdList() {
            return ArticleIdListObject;
        }

        public void setPublicationStatus(String PublicationStatus) {
            this.PublicationStatus = PublicationStatus;
        }

        public void setArticleIdList(ArticleIdList ArticleIdListObject) {
            this.ArticleIdListObject = ArticleIdListObject;
        }
    }
    public class ArticleIdList {
        ArrayList< Object > ArticleId = new ArrayList < Object > ();


        // Getter Methods



        // Setter Methods


    }

    @AllArgsConstructor
    @Builder
    @Data
    @XmlRootElement(name = "MedlineCitation")
    public static class MedlineCitation {
        PMID PMID;
        DateCompleted DateCompleted;
        DateRevised DateRevised;
        Article Article;
        MedlineJournalInfo MedlineJournalInfo;
        List<Chemical> ChemicalList;
        List<MeshHeading> MeshHeadingList;
        private String _Status;
        private String _Owner;

        public MedlineCitation() {
            ChemicalList = new ArrayList<>();
            MeshHeadingList = new ArrayList<>();
        }


        // Getter Methods

        public PMID getPMID() {
            return PMID;
        }

        public DateCompleted getDateCompleted() {
            return DateCompleted;
        }

        public DateRevised getDateRevised() {
            return DateRevised;
        }

        public Article getArticle() {
            return Article;
        }

        public MedlineJournalInfo getMedlineJournalInfo() {
            return MedlineJournalInfo;
        }

        public List<Chemical> getChemicalList() {
            return ChemicalList;
        }

        public List<MeshHeading> getMeshHeadingList() {
            return MeshHeadingList;
        }

        public String get_Status() {
            return _Status;
        }

        public String get_Owner() {
            return _Owner;
        }

        // Setter Methods

        public void setPMID(PMID PMIDObject) {
            this.PMID = PMIDObject;
        }

        public void setDateCompleted(DateCompleted DateCompletedObject) {
            this.DateCompleted = DateCompletedObject;
        }

        public void setDateRevised(DateRevised DateRevisedObject) {
            this.DateRevised = DateRevisedObject;
        }

        public void setArticle(Article ArticleObject) {
            this.Article = ArticleObject;
        }

        public void setMedlineJournalInfo(MedlineJournalInfo MedlineJournalInfoObject) {
            this.MedlineJournalInfo = MedlineJournalInfoObject;
        }

        public void setChemicalList(List<Chemical> ChemicalListObject) {
            this.ChemicalList = ChemicalListObject;
        }

        public void setMeshHeadingList(List<MeshHeading> MeshHeadingListObject) {
            this.MeshHeadingList = MeshHeadingListObject;
        }

        public void set_Status(String _Status) {
            this._Status = _Status;
        }

        public void set_Owner(String _Owner) {
            this._Owner = _Owner;
        }
    }
    public static class DescriptorName {
        private String _UI;
        private String _MajorTopicYN;
        private String __text;


        // Getter Methods

        public String get_UI() {
            return _UI;
        }

        public String get_MajorTopicYN() {
            return _MajorTopicYN;
        }

        public String get__text() {
            return __text;
        }

        // Setter Methods

        public void set_UI(String _UI) {
            this._UI = _UI;
        }

        public void set_MajorTopicYN(String _MajorTopicYN) {
            this._MajorTopicYN = _MajorTopicYN;
        }

        public void set__text(String __text) {
            this.__text = __text;
        }
    }

    public static class MeshHeadingContent{
        private String UI;
        private boolean majorTopic;
        private String content;

        public String getUI() {
            return UI;
        }

        public void setUI(String UI) {
            this.UI = UI;
        }

        public boolean isMajorTopic() {
            return majorTopic;
        }

        public void setMajorTopic(boolean majorTopic) {
            this.majorTopic = majorTopic;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
    @XStreamAlias("MeshHeading")
    public static class MeshHeading {
        List<MeshHeadingContent> DescriptorName;

        List<MeshHeadingContent> QualifierName;

        public MeshHeading() {
            DescriptorName = new ArrayList<>();
            QualifierName = new ArrayList<>();
        }

        public List<MeshHeadingContent> getDescriptorName() {
            return DescriptorName;
        }

        public List<MeshHeadingContent> getQualifierName() {
            return QualifierName;
        }
    }
    public static class Chemical {
        String RegistryNumber;
        String NameOfSubstance;

        public String getRegistryNumber() {
            return RegistryNumber;
        }

        public void setRegistryNumber(String registryNumber) {
            RegistryNumber = registryNumber;
        }

        public String getNameOfSubstance() {
            return NameOfSubstance;
        }

        public void setNameOfSubstance(String nameOfSubstance) {
            NameOfSubstance = nameOfSubstance;
        }

        // Getter Methods



        // Setter Methods


    }
    public static class MedlineJournalInfo {
        private String Country;
        private String MedlineTA;
        private String NlmUniqueID;
        private String ISSNLinking;


        // Getter Methods

        public String getCountry() {
            return Country;
        }

        public String getMedlineTA() {
            return MedlineTA;
        }

        public String getNlmUniqueID() {
            return NlmUniqueID;
        }

        public String getISSNLinking() {
            return ISSNLinking;
        }

        // Setter Methods

        public void setCountry(String Country) {
            this.Country = Country;
        }

        public void setMedlineTA(String MedlineTA) {
            this.MedlineTA = MedlineTA;
        }

        public void setNlmUniqueID(String NlmUniqueID) {
            this.NlmUniqueID = NlmUniqueID;
        }

        public void setISSNLinking(String ISSNLinking) {
            this.ISSNLinking = ISSNLinking;
        }
    }
    public static class Article {
        Journal Journal;
        private String ArticleTitle;
        Pagination Pagination;
        List<Author> AuthorList;
        private String Language;
        private String _PubModel;

        public Article() {
            AuthorList = new ArrayList<>();
        }

        // Getter Methods

        public Journal getJournal() {
            return Journal;
        }

        public String getArticleTitle() {
            return ArticleTitle;
        }

        public Pagination getPagination() {
            return Pagination;
        }

        public List<Author> getAuthorList() {
            return AuthorList;
        }

        public String getLanguage() {
            return Language;
        }

        public String get_PubModel() {
            return _PubModel;
        }

        // Setter Methods

        public void setJournal(Journal JournalObject) {
            this.Journal = JournalObject;
        }

        public void setArticleTitle(String ArticleTitle) {
            this.ArticleTitle = ArticleTitle;
        }

        public void setPagination(Pagination PaginationObject) {
            this.Pagination = PaginationObject;
        }

        public void setAuthorList(List<Author> AuthorListObject) {
            this.AuthorList = AuthorListObject;
        }

        public void setLanguage(String Language) {
            this.Language = Language;
        }

        public void set_PubModel(String _PubModel) {
            this._PubModel = _PubModel;
        }
    }

    public static class Grant {
        private String GrantID;
        private String Agency;
        private String Country;


        // Getter Methods

        public String getGrantID() {
            return GrantID;
        }

        public String getAgency() {
            return Agency;
        }

        public String getCountry() {
            return Country;
        }

        // Setter Methods

        public void setGrantID(String GrantID) {
            this.GrantID = GrantID;
        }

        public void setAgency(String Agency) {
            this.Agency = Agency;
        }

        public void setCountry(String Country) {
            this.Country = Country;
        }
    }

    public static class Author{
        private String LastName;
        private String ForeName;
        private String Initials;

        public String getLastName() {
            return LastName;
        }

        public void setLastName(String lastName) {
            LastName = lastName;
        }

        public String getForeName() {
            return ForeName;
        }

        public void setForeName(String foreName) {
            ForeName = foreName;
        }

        public String getInitials() {
            return Initials;
        }

        public void setInitials(String initials) {
            Initials = initials;
        }
    }
    public static class Pagination {
        private String MedlinePgn;


        // Getter Methods

        public String getMedlinePgn() {
            return MedlinePgn;
        }

        // Setter Methods

        public void setMedlinePgn(String MedlinePgn) {
            this.MedlinePgn = MedlinePgn;
        }
    }
    public static class Journal {
        ISSN ISSN;
        JournalIssue JournalIssue;
        private String Title;
        private String ISOAbbreviation;


        // Getter Methods

        public ISSN getISSN() {
            return ISSN;
        }

        public JournalIssue getJournalIssue() {
            return JournalIssue;
        }

        public String getTitle() {
            return Title;
        }

        public String getISOAbbreviation() {
            return ISOAbbreviation;
        }

        // Setter Methods

        public void setISSN(ISSN ISSNObject) {
            this.ISSN = ISSNObject;
        }

        public void setJournalIssue(JournalIssue JournalIssueObject) {
            this.JournalIssue = JournalIssueObject;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public void setISOAbbreviation(String ISOAbbreviation) {
            this.ISOAbbreviation = ISOAbbreviation;
        }
    }
    public static class JournalIssue {
        private String Volume;
        private String Issue;
        PubDate PubDate;
        private String _CitedMedium;


        // Getter Methods

        public String getVolume() {
            return Volume;
        }

        public String getIssue() {
            return Issue;
        }

        public PubDate getPubDate() {
            return PubDate;
        }

        public String get_CitedMedium() {
            return _CitedMedium;
        }

        // Setter Methods

        public void setVolume(String Volume) {
            this.Volume = Volume;
        }

        public void setIssue(String Issue) {
            this.Issue = Issue;
        }

        public void setPubDate(PubDate PubDateObject) {
            this.PubDate = PubDateObject;
        }

        public void set_CitedMedium(String _CitedMedium) {
            this._CitedMedium = _CitedMedium;
        }
    }
    public static class PubDate {
        private String Year;
        private String Month;


        // Getter Methods

        public String getYear() {
            return Year;
        }

        public String getMonth() {
            return Month;
        }

        // Setter Methods

        public void setYear(String Year) {
            this.Year = Year;
        }

        public void setMonth(String Month) {
            this.Month = Month;
        }
    }
    public static class ISSN {
        private String _IssnType;
        private String __text;


        // Getter Methods

        public String get_IssnType() {
            return _IssnType;
        }

        public String get__text() {
            return __text;
        }

        // Setter Methods

        public void set_IssnType(String _IssnType) {
            this._IssnType = _IssnType;
        }

        public void set__text(String __text) {
            this.__text = __text;
        }
    }
    public class DateRevised {
        private String Year;
        private String Month;
        private String Day;


        // Getter Methods

        public String getYear() {
            return Year;
        }

        public String getMonth() {
            return Month;
        }

        public String getDay() {
            return Day;
        }

        // Setter Methods

        public void setYear(String Year) {
            this.Year = Year;
        }

        public void setMonth(String Month) {
            this.Month = Month;
        }

        public void setDay(String Day) {
            this.Day = Day;
        }
    }
    public class DateCompleted {
        private String Year;
        private String Month;
        private String Day;


        // Getter Methods

        public String getYear() {
            return Year;
        }

        public String getMonth() {
            return Month;
        }

        public String getDay() {
            return Day;
        }

        // Setter Methods

        public void setYear(String Year) {
            this.Year = Year;
        }

        public void setMonth(String Month) {
            this.Month = Month;
        }

        public void setDay(String Day) {
            this.Day = Day;
        }
    }
    public static class PMID {
        private String ID;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }
    }
}
