package com.cscie599.gfn.importer.meshterm;

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
@XmlRootElement(name = "DescriptorRecord")
public class DescriptorRecord {

    private String DescriptorUI;
    DescriptorName DescriptorName;
    DateCreated DateCreated;
    DateRevised DateRevised;
    private String PublicMeSHNote;
    private TreeNumberList TreeNumberList;
    private String _DescriptorClass;


    // Getter Methods

    public String getDescriptorUI() {
        return DescriptorUI;
    }

    public DescriptorName getDescriptorName() {
        return DescriptorName;
    }

    public DateCreated getDateCreated() {
        return DateCreated;
    }

    public DateRevised getDateRevised() {
        return DateRevised;
    }

    public TreeNumberList getTreeNumberList() {
        return TreeNumberList;
    }

    public String get_DescriptorClass() {
        return _DescriptorClass;
    }

    public String getPublicMeSHNote() {
        return this.PublicMeSHNote;
    }



    // Setter Methods

    public void setDescriptorUI(String DescriptorUI) {
        this.DescriptorUI = DescriptorUI;
    }

    public void setDescriptorName(DescriptorName DescriptorName) {
        this.DescriptorName = DescriptorName;
    }

    public void setDateCreated(DateCreated DateCreated) {
        this.DateCreated = DateCreated;
    }

    public void setDateRevised(DateRevised DateRevised) {
        this.DateRevised = DateRevised;
    }


    public void setPublicMeSHNote(String PublicMeSHNote) {
        this.PublicMeSHNote = PublicMeSHNote;
    }

    public void setTreeNumberList(TreeNumberList TreeNumberList) {
        this.TreeNumberList = TreeNumberList;
    }


    public void set_DescriptorClass(String _DescriptorClass) {
        this._DescriptorClass = _DescriptorClass;
    }

    public static class TreeNumberList {
        private  ArrayList<String> TreeNumbers;

        public TreeNumberList() {
            TreeNumbers = new ArrayList <String>();
        }

        public ArrayList<String> getTreeNumbers() {
            return TreeNumbers;
        }

        public void setTreeNumbers(ArrayList<String> treeNumbers) {
            TreeNumbers = treeNumbers;
        }

        public String toString() {
            return "this is a TreeNumberList " + TreeNumbers.toString();
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

    public class DateCreated {
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

    public class DescriptorName {
        private String String;


        // Getter Methods

        public String getString() {
            return String;
        }

        // Setter Methods

        public void setString(String String) {
            this.String = String;
        }
    }


}