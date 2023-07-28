package crow.jonathan.toatracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class CrawlPanel extends JPanel
{
    private static final String ERROR_SOUND = "bonk";

    private PlayerTableModel playerTableModel;
    private GameState gameState;
    
    public CrawlPanel(GameState gameState)
    {
        this.gameState = gameState;
        playerTableModel = new PlayerTableModel();
        initComponents();
        setWeatherLabels();
        setTravelBoxes();
        DefaultCellEditor sizeEdit = new DefaultCellEditor(new JComboBox(CreatureSize.values()));
        plyrTable.getColumnModel().getColumn(PlayerTableModel.COL_SIZE).setCellEditor(sizeEdit);
        plyrTable.setDefaultRenderer(Float.class, playerCellRenderer);
    }
    public GameState getGameState()
    {
        return gameState;
    }
    private void setWeatherLabels()
    {
        Weather weather = gameState.getWeather();
        
        if(weather == null)
            return;
        
        tempLbl.setText(String.format("%s (%d)", weather.getTemperature().stringVal, weather.getTemperatureRoll()));
        rainLbl.setText(String.format("%s (%d)", weather.getRain().stringVal, weather.getRainRoll()));
        windLbl.setText(String.format("%s (%d)", weather.getWind().stringVal, weather.getWindRoll()));
    }
    private void setTravelBoxes()
    {
        Travel travel = gameState.getTravel();
        
        if(travel == null)
            return;
        
        terrainBox.setSelectedItem(travel.getTerrain());
        paceBox.setSelectedItem(travel.getPace());
        canoeCheck.setSelected(travel.hasCanoe());
        lostCheck.setSelected(travel.isLost());
    }
    private void weatherRollBtnActionPerformed(ActionEvent event)
    {
        gameState.setWeather(Weather.roll());
        setWeatherLabels();
        
        if(collectCheck.isSelected())
            gameState.triggerPlayerEvent(PlayerEvent.COLLECT);
        
        updateUI();
    }
    private void collectCheckActionPerformed(ActionEvent event)
    {
        collectBtn.setEnabled(!collectCheck.isSelected());
    }
    private void collectBtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.COLLECT);
        updateUI();
    }
    private void addPlyrBtnActionPerformed(ActionEvent event)
    {
        gameState.addPlayer();
        plyrTable.updateUI();
    }
    private void eatBtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.EAT, plyrTable.getSelectedRows());
        updateUI();
    }
    private void drinkBtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.DRINK, plyrTable.getSelectedRows());
        updateUI();
    }
    private void eatDrinkbtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.EAT, plyrTable.getSelectedRows());
        gameState.triggerPlayerEvent(PlayerEvent.DRINK, plyrTable.getSelectedRows());
        updateUI();
    }
    private void allEatBtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.EAT);
        updateUI();
    }
    private void allDrinkBtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.DRINK);
        updateUI();
    }
    private void allEatDrinkBtnActionPerformed(ActionEvent event)
    {
        gameState.triggerPlayerEvent(PlayerEvent.EAT);
        gameState.triggerPlayerEvent(PlayerEvent.DRINK);
        updateUI();
    }
    private void removePlyrBtnActionPerformed(ActionEvent event)
    {
        gameState.removePlayers(plyrTable.getSelectedRows());
        updateUI();
    }
    private void plyrTablePropertyChange(PropertyChangeEvent event)
    {
        updateUI();
    }
    private void terrainBoxActionPerformed(ActionEvent event)
    {
        gameState.getTravel().setTerrain((Travel.Terrain)terrainBox.getSelectedItem());
    }
    private void paceBoxActionPerformed(ActionEvent event)
    {
        gameState.getTravel().setPace((Travel.Pace)paceBox.getSelectedItem());
    }
    private void canoeCheckActionPerformed(ActionEvent event)
    {
        gameState.getTravel().setHasCanoe(canoeCheck.isSelected());
    }
    private void lostCheckActionPerformed(ActionEvent event)
    {
        gameState.getTravel().setLost(lostCheck.isSelected());
    }
    private void travelBtnActionPerformed(ActionEvent event)
    {
        wisRollLbl.setText("");
        paceLbl.setText("");
        travelLbl.setText("");
        
        Player nav = gameState.getNavigator();
        
        if(nav == null)
        {
            wisRollLbl.setText("Can't resolve travel. No navigator selected.");
            return;
        }
        
        Travel travel = gameState.getTravel();
        
        if(!travel.isLost())
        {
            int wisRoll = Dice.d20(gameState.getNavigatorWisdom()),
                wisDc = travel.getTerrain().navDc;

            wisRollLbl.setText(String.format("Navigator %s rolled %d against a DC %d", nav.getName(), wisRoll, wisDc));
            
            if(wisRoll < wisDc)
            {
                Sound.play(ERROR_SOUND);
                travel.setLost(true);
                lostCheck.setSelected(true);
            }
        }
        else
            wisRollLbl.setText("The party is already lost. Bypassing wisdom check.");
        
        if(travel.isLost())
        {
            Travel.Direction dir = Travel.Direction.getRandom();
            travelLbl.setText("The party is lost! They travel to the " + dir.stringVal);
        }
        else
        {
            Travel.Pace pace = travel.getPace();
            int tiles = (travel.hasCanoe() && travel.getTerrain().fasterByBoat) ? 2 : 1;
        
            if(pace != Travel.Pace.NORMAL)
            {
                int roll = Dice.d20();
                boolean pass = roll > 10;
                paceLbl.setText(String.format("The party rolled %d for their %s pace. (%s)", roll, pace.stringVal, pass ? "Passed" : "Failed"));

                if(pass)
                {
                    if(pace == Travel.Pace.FAST)
                        tiles++;
                    else
                        tiles--;
                }
            }
            
            travelLbl.setText("The party travels " + tiles + " tiles.");
        }
    }
    
    private final DefaultTableCellRenderer playerCellRenderer = new DefaultTableCellRenderer()
    {
        private static final Color DEFAULT_CELL_COLOR = Color.WHITE;
        private static final Color BAD_CELL_COLOR = Color.RED.brighter();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object val, boolean selected, boolean focus, int row, int col)
        {
            Component comp = super.getTableCellRendererComponent(table, val, selected, focus, row, col);
            Player plyr = gameState.getPlayer(row);
            
            if(plyr == null)
                return comp;
            
            boolean extreme = gameState.getWeather().getTemperature() == Weather.Temperature.EXTREME_HEAT;
            
            switch(col)
            {
                case PlayerTableModel.COL_FOOD:
                    if(plyr.hasEnoughFood(extreme))
                        comp.setBackground(DEFAULT_CELL_COLOR);
                    else
                        comp.setBackground(BAD_CELL_COLOR);
                    break;
                case PlayerTableModel.COL_WATER:
                    if(plyr.hasEnoughWater(extreme))
                        comp.setBackground(DEFAULT_CELL_COLOR);
                    else
                        comp.setBackground(BAD_CELL_COLOR);
            }
            
            return comp;
        }
    };
    
    private class PlayerTableModel extends AbstractTableModel
    {
        private static final int COLUMN_COUNT = 7;

        private static final int COL_NAME      = 0;
        private static final int COL_SIZE      = 1;
        private static final int COL_FOOD      = 2;
        private static final int COL_WATER     = 3;
        private static final int COL_COLLECTOR = 4;
        private static final int COL_WISDOM    = 5;
        private static final int COL_NAV       = 6;
        
        private boolean rowInBounds(int row)
        {
            return row >= 0 && row < getRowCount();
        }
        private boolean colInBounds(int col)
        {
            return col >= 0 && col < getColumnCount();
        }
        private boolean isInBounds(int row, int col)
        {
            return rowInBounds(row) && colInBounds(col);
        }
        @Override
        public int getRowCount()
        {
            return gameState.getPlayerCount();
        }
        @Override
        public int getColumnCount()
        {
            return COLUMN_COUNT;
        }
        @Override
        public Object getValueAt(int row, int col)
        {
            if(!rowInBounds(row))
                return null;
            
            Player plyr = gameState.getPlayer(row);
            switch(col)
            {
                case COL_NAME:
                    return plyr.getName();
                case COL_SIZE:
                    return plyr.getSize();
                case COL_FOOD:
                    return String.format("%.2f", plyr.getFood());
                case COL_WATER:
                    return String.format("%.2f", plyr.getWater());
                case COL_COLLECTOR:
                    return plyr.hasCollector();
                case COL_WISDOM:
                    return plyr.getWisdomModifier();
                case COL_NAV:
                    return plyr.isNavigator();
                default:
                    return null;
            }
        }
        @Override
        public String getColumnName(int col)
        {
            switch(col)
            {
                case COL_NAME:
                    return "Player Name";
                case COL_SIZE:
                    return "Size";
                case COL_FOOD:
                    return "Food";
                case COL_WATER:
                    return "Water";
                case COL_COLLECTOR:
                    return "Has Collector";
                case COL_WISDOM:
                    return "Wisdom Modifier";
                case COL_NAV:
                    return "Is Navigator";
                default:
                    return "";
            }
        }
        @Override
        public Class<?> getColumnClass(int col)
        {
            switch(col)
            {
                case COL_NAME:
                    return String.class;
                case COL_SIZE:
                    return CreatureSize.class;
                case COL_FOOD:
                case COL_WATER:
                    return Float.class;
                case COL_WISDOM:
                    return Integer.class;
                case COL_COLLECTOR:
                case COL_NAV:
                    return Boolean.class;
                default:
                    return Object.class;
            }
        }
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return true;
        }
        @Override
        public void setValueAt(Object val, int row, int col)
        {
            if(!isInBounds(row, col))
                return;
            
            Player plyr = gameState.getPlayer(row);
            
            switch(col)
            {
                case COL_NAME:
                    if(val instanceof String)
                        plyr.setName((String)val);
                    break;
                case COL_SIZE:
                    if(val instanceof CreatureSize)
                        plyr.setSize((CreatureSize)val);
                    break;
                case COL_FOOD:
                    if(val instanceof Float)
                        plyr.setFood((float)val);
                    break;
                case COL_WATER:
                    if(val instanceof Float)
                        plyr.setWater((float)val);
                    break;
                case COL_COLLECTOR:
                    if(val instanceof Boolean)
                        plyr.setCollector((boolean)val);
                    break;
                case COL_WISDOM:
                    if(val instanceof Integer)
                        plyr.setWisdomModifier((int)val);
                    break;
                case COL_NAV:
                    if(val instanceof Boolean)
                    {
                        if((boolean)val)
                        {
                            gameState.setNavigator(row);
                            updateUI();
                        }
                    }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rainLbl = new javax.swing.JLabel();
        tempLbl = new javax.swing.JLabel();
        windLbl = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        plyrTable = new javax.swing.JTable();
        addPlyrBtn = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        eatBtn = new javax.swing.JButton();
        drinkBtn = new javax.swing.JButton();
        eatDrinkbtn = new javax.swing.JButton();
        removePlyrBtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        allEatBtn = new javax.swing.JButton();
        allDrinkBtn = new javax.swing.JButton();
        allEatDrinkBtn = new javax.swing.JButton();
        collectCheck = new javax.swing.JCheckBox();
        weatherRollBtn = new javax.swing.JButton();
        collectBtn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        terrainBox = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        paceBox = new javax.swing.JComboBox<>();
        canoeCheck = new javax.swing.JCheckBox();
        lostCheck = new javax.swing.JCheckBox();
        travelBtn = new javax.swing.JButton();
        travelLbl = new javax.swing.JLabel();
        wisRollLbl = new javax.swing.JLabel();
        paceLbl = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(750, 500));

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel1.setText("Weather");

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel2.setText("Temperature:");

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel3.setText("Wind:");

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel4.setText("Precipitation:");

        rainLbl.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        rainLbl.setText("N/A");

        tempLbl.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        tempLbl.setText("N/A");

        windLbl.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        windLbl.setText("N/A");

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel5.setText("Players");

        plyrTable.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        plyrTable.setModel(playerTableModel);
        plyrTable.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                plyrTablePropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(plyrTable);

        addPlyrBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        addPlyrBtn.setText("Add Player");
        addPlyrBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addPlyrBtnActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel6.setText("Selected Players:");

        eatBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        eatBtn.setText("Eat");
        eatBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                eatBtnActionPerformed(evt);
            }
        });

        drinkBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        drinkBtn.setText("Drink");
        drinkBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                drinkBtnActionPerformed(evt);
            }
        });

        eatDrinkbtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        eatDrinkbtn.setText("Eat & Drink");
        eatDrinkbtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                eatDrinkbtnActionPerformed(evt);
            }
        });

        removePlyrBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        removePlyrBtn.setText("Remove");
        removePlyrBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                removePlyrBtnActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel7.setText("All Players:");

        allEatBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        allEatBtn.setText("Eat");
        allEatBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                allEatBtnActionPerformed(evt);
            }
        });

        allDrinkBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        allDrinkBtn.setText("Drink");
        allDrinkBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                allDrinkBtnActionPerformed(evt);
            }
        });

        allEatDrinkBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        allEatDrinkBtn.setText("Eat & Drink");
        allEatDrinkBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                allEatDrinkBtnActionPerformed(evt);
            }
        });

        collectCheck.setSelected(true);
        collectCheck.setText("Auto-collect Water");
        collectCheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                collectCheckActionPerformed(evt);
            }
        });

        weatherRollBtn.setText("Roll");
        weatherRollBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                weatherRollBtnActionPerformed(evt);
            }
        });

        collectBtn.setText("Collect Water");
        collectBtn.setEnabled(false);
        collectBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                collectBtnActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel8.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel8.setText("Travel");

        jLabel9.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel9.setText("Terrain:");

        terrainBox.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        terrainBox.setModel(new DefaultComboBoxModel(Travel.Terrain.values()));
        terrainBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                terrainBoxActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jLabel10.setText("Pace:");

        paceBox.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        paceBox.setModel(new DefaultComboBoxModel(Travel.Pace.values()));
        paceBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                paceBoxActionPerformed(evt);
            }
        });

        canoeCheck.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        canoeCheck.setText("In Canoe");
        canoeCheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                canoeCheckActionPerformed(evt);
            }
        });

        lostCheck.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        lostCheck.setText("Lost");
        lostCheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                lostCheckActionPerformed(evt);
            }
        });

        travelBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        travelBtn.setText("Travel");
        travelBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                travelBtnActionPerformed(evt);
            }
        });

        travelLbl.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        travelLbl.setText(" ");

        wisRollLbl.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        wisRollLbl.setText(" ");

        paceLbl.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        paceLbl.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(windLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                    .addComponent(rainLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(weatherRollBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(collectBtn))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(eatBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(drinkBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(eatDrinkbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removePlyrBtn))
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tempLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(collectCheck))
                            .addComponent(jLabel5)
                            .addComponent(addPlyrBtn)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(40, 40, 40)
                                .addComponent(allEatBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(allDrinkBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(allEatDrinkBtn))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(travelLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(terrainBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(paceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(canoeCheck)
                            .addComponent(lostCheck)
                            .addComponent(travelBtn))
                        .addGap(0, 158, Short.MAX_VALUE))
                    .addComponent(wisRollLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paceLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(terrainBox, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(paceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(canoeCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lostCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(travelBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wisRollLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paceLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(travelLbl)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(tempLbl)
                            .addComponent(collectCheck))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(windLbl)
                            .addComponent(weatherRollBtn)
                            .addComponent(collectBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(rainLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addPlyrBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(eatBtn)
                            .addComponent(drinkBtn)
                            .addComponent(eatDrinkbtn)
                            .addComponent(removePlyrBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(allEatBtn)
                            .addComponent(allDrinkBtn)
                            .addComponent(allEatDrinkBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    /*
    private void addPlyrBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addPlyrBtnActionPerformed
    {//GEN-HEADEREND:event_addPlyrBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addPlyrBtnActionPerformed

    private void eatBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_eatBtnActionPerformed
    {//GEN-HEADEREND:event_eatBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eatBtnActionPerformed

    private void drinkBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_drinkBtnActionPerformed
    {//GEN-HEADEREND:event_drinkBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drinkBtnActionPerformed

    private void eatDrinkbtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_eatDrinkbtnActionPerformed
    {//GEN-HEADEREND:event_eatDrinkbtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eatDrinkbtnActionPerformed

    private void removePlyrBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removePlyrBtnActionPerformed
    {//GEN-HEADEREND:event_removePlyrBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_removePlyrBtnActionPerformed

    private void allEatBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_allEatBtnActionPerformed
    {//GEN-HEADEREND:event_allEatBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_allEatBtnActionPerformed

    private void allDrinkBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_allDrinkBtnActionPerformed
    {//GEN-HEADEREND:event_allDrinkBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_allDrinkBtnActionPerformed

    private void allEatDrinkBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_allEatDrinkBtnActionPerformed
    {//GEN-HEADEREND:event_allEatDrinkBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_allEatDrinkBtnActionPerformed

    private void weatherRollBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_weatherRollBtnActionPerformed
    {//GEN-HEADEREND:event_weatherRollBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_weatherRollBtnActionPerformed

    private void plyrTablePropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_plyrTablePropertyChange
    {//GEN-HEADEREND:event_plyrTablePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_plyrTablePropertyChange

    private void collectCheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_collectCheckActionPerformed
    {//GEN-HEADEREND:event_collectCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_collectCheckActionPerformed

    private void collectBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_collectBtnActionPerformed
    {//GEN-HEADEREND:event_collectBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_collectBtnActionPerformed

    private void terrainBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_terrainBoxActionPerformed
    {//GEN-HEADEREND:event_terrainBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_terrainBoxActionPerformed

    private void paceBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_paceBoxActionPerformed
    {//GEN-HEADEREND:event_paceBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paceBoxActionPerformed

    private void canoeCheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_canoeCheckActionPerformed
    {//GEN-HEADEREND:event_canoeCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_canoeCheckActionPerformed

    private void lostCheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lostCheckActionPerformed
    {//GEN-HEADEREND:event_lostCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lostCheckActionPerformed

    private void travelBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_travelBtnActionPerformed
    {//GEN-HEADEREND:event_travelBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_travelBtnActionPerformed
*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPlyrBtn;
    private javax.swing.JButton allDrinkBtn;
    private javax.swing.JButton allEatBtn;
    private javax.swing.JButton allEatDrinkBtn;
    private javax.swing.JCheckBox canoeCheck;
    private javax.swing.JButton collectBtn;
    private javax.swing.JCheckBox collectCheck;
    private javax.swing.JButton drinkBtn;
    private javax.swing.JButton eatBtn;
    private javax.swing.JButton eatDrinkbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JCheckBox lostCheck;
    private javax.swing.JComboBox<String> paceBox;
    private javax.swing.JLabel paceLbl;
    private javax.swing.JTable plyrTable;
    private javax.swing.JLabel rainLbl;
    private javax.swing.JButton removePlyrBtn;
    private javax.swing.JLabel tempLbl;
    private javax.swing.JComboBox<String> terrainBox;
    private javax.swing.JButton travelBtn;
    private javax.swing.JLabel travelLbl;
    private javax.swing.JButton weatherRollBtn;
    private javax.swing.JLabel windLbl;
    private javax.swing.JLabel wisRollLbl;
    // End of variables declaration//GEN-END:variables
}
