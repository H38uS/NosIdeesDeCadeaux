<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"><html xmlns="http://www.w3.org/1999/xhtml"> 
<html xmlns="http://www.w3.org/1999/xhtml"> 
    <head>  
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
        <title>Idées cadeaux</title>
        <link rel="shortcut icon" href="/image/cadeaux.ico"/>
        <script src="<%=application.getContextPath()%>/js/lib/jquery.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/lib/jquery_autocomplete.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/lib/jquery.tooltipster.min.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/lib/thickbox.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/global.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/loading.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/reservation/reservation.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/idees/modifier.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/idees/ajouter.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/friendnetwork/ajouterliste.js" type="text/javascript"></script>
        <script src="<%=application.getContextPath()%>/js/friendnetwork/suppressionListe.js" type="text/javascript"></script>
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/common.css" />
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/normal/global.css" />
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css//normal/menu.css" />
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css//normal/friendnetwork.css" />
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/lib/thickbox.css" />   
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/lib/jquery.autocomplete.css" />
        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/lib/tooltipster.css" />
    </head> 
    <body>
    <%--
        <?php if ($this->layout()->animate != null) { ?>
        <script type="text/javascript">
            $(document).ready(function () {
                goToCadeau("<?php echo $this->layout()->animate; ?>");
            });
        </script>
        <?php } ?>
        <div id="header">
            <?php $auth = Zend_Auth::getInstance(); ?>
            <div id="leftPart">
                <a href="<?php echo $this->url(array('controller' => 'index', 'action' => 'personnesaafficher'), "", true); ?>" 
                   class="thickbox">Sélectionner</a> les personnes à afficher.
                
                <br />
                Aller voir les cadeaux de 
                <select id="layout_owner">
                        <option value=""></option>
                    <?php foreach ($this->layout()->owners as $owner) { ?>
                        <option value="<?php echo $owner; ?>"><?php echo ucfirst($owner); ?></option>
                    <?php } ?>  
                </select><br />
                <a href="<?php echo $this->url(array('controller' => 'idees', 'action' => 'ajouter', 'choice' => 'true'), "", true); ?>" 
                   class="thickbox img">
                    <img src="/image/ajouter_a_personne.png" 
                         alt="Ajouter une idée à..." 
                         title="Ajouter une idée à..." />
                </a>
                <a href="<?php echo $this->url(array('controller' => 'idees', 'action' => 'ajouter', 'personne' => $auth->getIdentity()->username), "", true); ?>" class="thickbox">
                    <img src="/image/ajouter.png" 
                         alt="S'ajouter une nouvelle idée" 
                         title="S'ajouter une nouvelle idée" />
                </a>
            </div>
            <div id="rightPart">
                <?php if ($auth->hasIdentity()) { ?>
                Connecté en tant que <?php echo $auth->getIdentity()->username; ?> - 
                <a href="<?php echo $this->url(array('controller' => 'login', 'action' => 'logout'), "", true); ?>">se déconnecter</a>.<br/>
                <?php } ?>
                Un doute sur le fonctionnement ? Jetez un oeil à la 
                <a href="/documentation.pdf">documentation</a>.
                <div>
                    <a href="<?php echo $this->url(array('controller' => 'friendnetwork', 'action' => 'ajouterliste'), "", true); ?>" 
                    class="thickbox img">
                        <img src="/image/ajouter_liste.png" 
                             alt="Ajouter la liste d'une personne." 
                             title="Ajouter la liste de cadeaux d'une personne" />
                    </a>  
                    <a href="<?php echo $this->url(array('controller' => 'friendnetwork', 'action' => 'demandesenvoyees'), "", true); ?>" 
                    class="thickbox img">
                        <img src="/image/demandes_envoyees.png" 
                             alt="Voir les demandes envoyées." 
                             title="Voir les demandes envoyées" />
                    </a>     
                    <a href="<?php echo $this->url(array('controller' => 'login', 
                                                         'action'     => 'changepwd'), "", true); ?>" 
                       class="img">
                        <img src="/image/change_pwd.png" 
                             alt="Modifier mon mot de passe." 
                             title="Modifier mon mot de passe" />
                    </a>  
                    <a href="<?php echo $this->url(array('controller' => 'login', 
                                                         'action'     => 'changeemail'), "", true); ?>" 
                       class="img">
                        <img src="/image/change_email.png" 
                             alt="Modifier mon adresse mail." 
                             title="Modifier mon adresse mail" />
                    </a>  
                    <?php if (isset($this->hasMasterRights) && $this->hasMasterRights === true) { ?>
                    <a href="<?php echo $this->url(array('controller' => 'login', 
                                                         'action'     => 'resetpwd'), "", true); ?>" 
                       class="thickbox img">
                        <img src="/image/ChangePassword.png" 
                             alt="Remise à zéro du mot de passe." 
                             title="Remise à zéro du mot de passe" />
                    </a>
                    <a href="<?php echo $this->url(array('action' => 'ajouterpersonne'), "", true); ?>" 
                       class="thickbox img">
                        <img src="/image/ajouter_personne.png" 
                             alt="Ajouter une nouvelle personne." 
                             title="Ajouter une nouvelle personne" />
                    </a>
                    <?php } ?>
                </div>
            </div>
        </div>
        <div id="hideShowNotif">
            <?php if ($this->hasNotif) { ?>
            <div>                
                Masquer les <br /> notifications
            </div>
            <?php } ?>
        </div>
        <div id="notif">
        <?php if (sizeof($this->notifs) > 0) { ?>
        <table>
            <?php foreach ($this->notifs as $notif) { ?>
            <tr>
                <td>
                    <img alt="Notification" 
                         title="Notification" 
                         src="/image/<?php echo $notif->getIcone(); ?>" 
                    />
                </td>
                <td>
                    <?php echo $notif->getNotifText();?>
                </td>
                <td>
                    <?php if (get_class($notif) === "DemandeRecue") { ?>
                        <?php echo $notif->getAcceptString($this); ?>
                    <?php } ?>
                </td>
                <td>
                    <?php if ($notif->canBeRemoved()) { ?>
                        <?php echo $notif->getRemovalString($this); ?>
                    <?php } ?>
                </td>
            </tr>
            <?php } ?>
        </table>
        <?php } ?>
        </div>
        <div id="loading"></div>--%>
        <div id="body">
            <div id="title">
                <h1>Bienvenue sur le site de nos idées cadeaux !</h1>              
            </div>
            <jsp:doBody/>
		</div>
	</body>
</html>
