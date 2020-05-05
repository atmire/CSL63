- [Introduction](#Introduction)
- [Acknowledgement](#Acknowledgement)
- [Patch Installation Procedures](#Patch-installation-procedures)
	- [Prerequisites](#Prerequisites)
	- [Obtaining a recent patch file](#Obtaining-recent-patch)
	- [Patch installation](#Patch-installation)
		- [1. Go to the DSpace Source directory.](#goto-DSpace-Source)
		- [2. Run the Git command to check whether the patch can be correctly applied.](#Run-git-command)
		- [3. Apply the patch](#Apply-patch)
		- [4. Rebuild and redeploy your repository](#Rebuild-redeploy)
		- [5. Restart your tomcat](#Restart-tomcat)
	- [Configure the metadata mapping](#Metadata-mapping)
	- [CSL management](#CSL-management)
	- [Exporting citations](#Exporting-citations)
- [Verification](#Verification)

# Introduction <a name="Introduction"></a>

The CSL patch adds support for CSL exports in DSpace (designed for DSpace 6, developed and tested against DSpace 6.3).
For more information about the Citation Style Language, please refer to https://citationstyles.org/.

# Acknowledgement <a name="Acknowledgement"></a>

The CSL patch has been funded by the University of Bordeaux (www.u-bordeaux.com) and developed by Atmire (https://www.atmire.com/).

# Patch Installation Procedures <a name="Patch-installation-procedures"></a>

## Prerequisites  <a name="Prerequisites"></a>

The CSL changes have been released as a "patch" for DSpace as this allows for the easiest installation process of the incremental codebase.

**__Important note__**: Below, we will explain you how to apply the patch to your existing installation. This will affect your source code. Before applying a patch, it is **always** recommended to create backup of your DSpace source code.

In order to apply the patch, you will need to locate the **DSpace source code** on your server. That source code directory contains a directory _dspace_, as well as the following files:  _LICENSE_,  _NOTICE_ ,  _README_ , ....

For every release of DSpace, generally two release packages are available. One package has "src" in its name and the other one doesn't. The difference is that the release labelled "src" contains ALL of the DSpace source code, while the other release retrieves precompiled packages for specific DSpace artifacts from maven central. **The CSL changes were designed to work on both "src" and other release packages of DSpace**. 

To be able to install the patch, you will need the following prerequisites:

* A running DSpace 6.3 instance. 
* Git should be installed on the machine. The patch will be applied using several git commands as indicated in the next section. 

## Obtaining a recent patch file <a name="Obtaining-recent-patch"></a>

Atmire's modifications to a standard DSpace are tracked on Github. The newest patch can therefore be generated from git.

DSPACE 6.3 [https://github.com/atmire/CSL63/compare/813800ce1736ec503fdcfbee4d86de836788f87c...master.diff](https://github.com/atmire/CSL63/compare/813800ce1736ec503fdcfbee4d86de836788f87c...master.diff)

## Patch installation <a name="Patch-installation"></a>

To install the patch, the following steps will need to be performed. 

### 1. Go to the DSpace Source directory. <a name="goto-DSpace-Source"></a>

This folder should have a structure similar to:   
dspace  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   modules  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    config  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    ...  
pom.xml


### 2. Run the Git command to check whether the patch can be correctly applied. <a name="Run-git-command"></a>

Run the following command where <patch file> needs to be replaced with the name of the patch:

``` 
git apply --check <patch file>
```

This command will return whether it is possible to apply the patch to your installation. This should pose no problems in case the DSpace is not customized or in case not much customizations are present.   
In case, the check is successful, the patch can be installed without any problems. Otherwise, you will have to merge some changes manually.

### 3. Apply the patch <a name="Apply-patch"></a>

To apply the patch, the following command should be run where <patch file> is replaced with the name of the patch file. 

``` 
git apply --whitespace=nowarn --reject <patch file>
```

This command will tell git to apply the patch and ignore unharmful whitespace issues. The `--reject` flag instructs the command to continue when conflicts are encountered and saves the corresponding code hunks to a `.rej` file so you can review and apply them manually later on. Before continuing to the next step, you have to resolve all merge conflicts indicated by the `.rej` files. After solving the merge conflicts, remove all the `.rej` files.

### 4. Rebuild and redeploy your repository <a name="Rebuild-redeploy"></a>

After the patch has been applied, the repository will need to be rebuild.   
DSpace repositories are typically built using the Maven and deployed using Ant. 

### 5. Restart your tomcat <a name="Restart-tomcat"></a>

After the repository has been rebuild and redeployed, the tomcat will need to be restarted to bring the changes to production.

## Configure the metadata mapping <a name="Metadata-mapping"></a>

The configuration of this functionality is located in the dspace/config/spring/api/csl-citation.xml file. This contains a bean with id DSpaceListItemDataProvider which will define how the citations are made. To configure this, we'll need to add properties with a certain value.

The property name that should be defined is the type of field within the citation that you want to see filled in, the way how this is filled in is determined by the citation file that the user selects. The value of this field is defined by the config namely by entering a metadata field in the value of the property. This metadata field will be retrieved for the items for which a citation is created, the value of this metadata field will be filled in into the citation's corresponding property.

For example if we want to fill in the title for a citation, we define a property with name 'title' and value 'dc.title'. This will fill in the title in the citation with the value retrieved in the dc.title metadata field for an item. Example properties with all possible names are provided in comments.

The property with the name "type" is special. The value of the metadata field referenced by this property is used to determine the format of the citation. To make a link between the custom types defined in your repository, a mapping is made between your repository's types, and the types supported by CSL. This mapping is made in the `<util:map id="CSLTypeMap">...</util:map>` element of the same configuration file. This element contains entries, for which the keys are the your repository's types, and the values are the corresponding CSL supported types. Take for example `<entry key="Article" value="#{ T(de.undercouch.citeproc.csl.CSLType).ARTICLE}"/>`. Here, the "ARTICLE" is the type as supported by CSL.

The limited list of possible values is: `ARTICLE, ARTICLE_JOURNAL, ARTICLE_MAGAZINE, ARTICLE_NEWSPAPER, BILL, BOOK, BROADCAST, CHAPTER, DATASET, ENTRY, ENTRY_DICTIONARY, ENTRY_ENCYCLOPEDIA, FIGURE, GRAPHIC, INTERVIEW, LEGAL_CASE, LEGISLATION, MANUSCRIPT, MAP, MOTION_PICTURE, MUSICAL_SCORE, PAMPHLET, PAPER_CONFERENCE, PATENT, PERSONAL_COMMUNICATION, POST, POST_WEBLOG, REPORT, REVIEW, REVIEW_BOOK, SONG, SPEECH, THESIS, TREATY, WEBPAGE`

The available export formats are configured in the dspace/config/spring/xmlui/csl-citation-formats.xml file. A list of available formats is specified, and for each format, the format name and the file extension is specified. The label for these formats can be found in the messages file using `xmlui.citation.format.` followed by the format.
Example: `<message key="xmlui.citation.format.htmlFormat">HTML</message>`

Currently, the following formats are configured:
```
       <util:list id="citationFormatList" value-type="com.atmire.app.xmlui.aspect.citations.CitationFormat">
           <ref bean="htmlFormat"/>
           <ref bean="textFormat"/>
           <ref bean="asciidocFormat"/>
           <ref bean="foFormat"/>
           <ref bean="rtfFormat"/>
       </util:list>
   
       <bean id="htmlFormat" class="com.atmire.app.xmlui.aspect.citations.CitationFormat">
           <property name="format" value="html"/>
           <property name="fileExtension" value="html"/>
       </bean>
   
       <bean id="textFormat" class="com.atmire.app.xmlui.aspect.citations.CitationFormat">
           <property name="format" value="text"/>
           <property name="fileExtension" value="txt"/>
       </bean>
   
       <bean id="asciidocFormat" class="com.atmire.app.xmlui.aspect.citations.CitationFormat">
           <property name="format" value="asciidoc"/>
           <property name="fileExtension" value="txt"/>
       </bean>
   
       <bean id="foFormat" class="com.atmire.app.xmlui.aspect.citations.CitationFormat">
           <property name="format" value="fo"/>
           <property name="fileExtension" value="fo"/>
       </bean>
   
       <bean id="rtfFormat" class="com.atmire.app.xmlui.aspect.citations.CitationFormat">
           <property name="format" value="rtf"/>
           <property name="fileExtension" value="rtf"/>
       </bean>
```

## CSL management <a name="CSL-management"></a>

A page has been added at "/citations" to manage the available citation formats. This page is available for any logged in user, and a link to it has been added in the sidebar under "my account".

The page displays a table of all citations visible to that user, with the possibility to view, edit or delete them. It also contains a form to upload a new citation format. The new citation can be either global (visible to all users) or personal (only visible to the current user and to admins).

Only admins can create global citation formats, regular users won't see this checkbox.

Styles can be downloaded from https://github.com/citation-style-language/styles

## Exporting citations <a name="Exporting-citations"></a>

A button named "Citations" has been added to the top of the item view page and the search results list. Users can export citations by clicking that button and choosing their desired style. Logged out users will only see the global styles, logged in users will also see their personal styles. After selecting the desired style, the user needs to select his desired format in the dialog.

# Verification <a name="Verification"></a>

Browse to your DSpace repository, make sure you're logged in and navigate to the "Citations format" page from the sidebar under "my account". You should see a page with a form to add a new style.

For some of the following tests you will need to be logged in as a submitter, and for the rest you will need to login as an administrator. For all of the following tests you should start by browsing to your repository's home page. Then in the sidebar, under "my account", follow the new "Citations format" link.

| Logged in user | Test description |
| -------------- | ---------------- |
| Submitter | Click the add new style button. An error notification should be shown because you didn't select a file. |
| Submitter | Choose a file and click the "add new style" button. A new style should be visible in the overview table. It should not be marked as global. The alias should be the filename. |
| Submitter | Choose a file, enter an alias and click the "add new style" button. A new style with the given alias should be visible in the overview table. It should not be marked as global. |
| Administrator | Just visit the citations page. The styles submitted by the other user should no longer be visible. |
| Administrator | Choose a file, enter an alias and click the "add new style" button. A new style with the given alias should be visible in the overview table. It should not be marked as global. |
| Administrator | Choose a file, enter an alias, check the global checkbox and click the "add new style" button. A new style with the given alias should be visible in the overview table. It should be marked as global. |
| Administrator | Choose a file, enter an alias for which a personal style already exists and click the "add new style" button. An error notification should be shown because a personal style with this alias already exists. |
| Administrator | Choose a file, enter an alias for which a global style already exists, check the global checkbox and click the "add new style" button. An error notification should be shown because a global style with this alias already exists. |
| Submitter | Select some personal styles and click the "delete styles" button. A success notification should be shown. The selected styles should be deleted. |
| Administrator | Select some personal and global styles and click the "delete styles" button. A success notification should be shown. The selected styles should be deleted. |
| Submitter | Click the edit button next to a personal style. An edit page should be shown, with the style filename as a link, the style alias as a textbox. |
| Submitter | Click the edit button next to a personal style. Click the style filename link. The style file should be downloaded. |
| Submitter | Click the edit button next to a personal style. Click the cancel button. The citation formats overview page should be shown. |
| Submitter | Click the edit button next to a personal style. Click the save button. The citation formats overview page should be shown. |
| Submitter | Click the edit button next to a personal style. Alter the alias and click the save button. The citation formats overview page should be shown. The alias should be changed. |
| Submitter | Click the edit button next to a personal style. Enter an alias for which a personal style already exists. Click the save button. The citation formats overview page should be shown. An error notification should be shown because a personal style with this alias already exists. |
| Administrator | Click the edit button next to a personal style. An edit page should be shown, with the style filename as a link, the style alias as a textbox. A checkbox should be shown but not checked. |
| Administrator | Click the edit button next to a global style. An edit page should be shown, with the style filename as a link, the style alias as a textbox. A checkbox should be shown and selected. Click the save button. |
| Administrator | Click the edit button next to a personal style. Check the global checkbox and click the save button. The style should now be global. |
| Administrator | Just visit the citations page. You should see an overview of your available citation styles. No checkboxes or edit buttons should be disabled. There should be a form to add a new style: a file chooser, a field for an alias and a checkbox to make the style global. |
| Submitter | Just visit the citations page. You should see an overview of your available citation styles. The checkboxes for the global styles should be disabled, the edit buttons for these styles should be disabled as well. There should be a form to add a new style: a file chooser and a field to enter an alias. |
| Administrator | Select all styles and click the "delete styles" button. The overview table should be gone. |
| Administrator | Attempt to upload a file that doesn't have the .csl extension An error should be displayed that only csl files are supported. |
